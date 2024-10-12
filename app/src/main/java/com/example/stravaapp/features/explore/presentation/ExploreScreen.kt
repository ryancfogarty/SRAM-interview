package com.example.stravaapp.features.explore.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.stravaapp.R
import com.example.stravaapp.common.presentation.component.CtaButton
import com.example.stravaapp.features.explore.data.repository.LatLong
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.explore)
                    )
                },
                navigationIcon = {
                    IconButton(onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ExploreScreenContent(
                state = viewModel.uiState,
                modifier = Modifier.weight(1f),
            )

            ExploreSegmentsCta(
                onLocationUpdate = { location ->
                    viewModel.exploreSegments(
                        southwestBound = LatLong(
                            lat = (location.latitude - 1).toFloat(),
                            long = (location.longitude - 1).toFloat(),
                        ),
                        northeastBound = LatLong(
                            lat = (location.latitude + 1).toFloat(),
                            long = (location.longitude + 1).toFloat(),
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun ExploreScreenContent(
    state: State,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (state) {
            is State.Success -> {
                SegmentList(segments = state.segments)
            }

            is State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is State.Error -> {
                Text(
                    text = stringResource(R.string.oops_error_retry),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun SegmentList(
    segments: List<SegmentUI>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Text(stringResource(R.string.nearby_segments))
        }
        items(segments) { segment ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = segment.name,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(stringResource(R.string.grade_avg_x, segment.averageGrade))
                    Text(stringResource(R.string.distance_x_meters, segment.distance))
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ExploreSegmentsCta(
    onLocationUpdate: (Location) -> Unit,
) {
    val context = LocalContext.current
    val permissionRequest = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    var requestedPermissions: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(permissionRequest.allPermissionsGranted, requestedPermissions) {
        if (permissionRequest.allPermissionsGranted && requestedPermissions) {
            getLastLocation(context, onLocationUpdate)
        }
    }

    CtaButton(
        onClick = {
            if (!permissionRequest.allPermissionsGranted) {
                requestedPermissions = true
                permissionRequest.launchMultiplePermissionRequest()
            } else {
                getLastLocation(context, onLocationUpdate)
            }
        },
        title = stringResource(R.string.explore_segments_near_me)
    )
}

@RequiresPermission(
    allOf = [
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ]
)
private fun getLastLocation(context: Context, onSuccess: (Location) -> Unit) {
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation
        .addOnSuccessListener(onSuccess)
}