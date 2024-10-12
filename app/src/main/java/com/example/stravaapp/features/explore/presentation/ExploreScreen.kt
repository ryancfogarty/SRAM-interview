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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.stravaapp.R
import com.example.stravaapp.common.presentation.component.CtaButton
import com.example.stravaapp.features.explore.data.repository.LatLong
import com.example.stravaapp.features.explore.data.repository.Segment
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onBack: () -> Unit,
) {
    val state = viewModel.uiState.collectAsState().value

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
                state = state,
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
                // TODO
            }
        }
    }
}

@Composable
private fun SegmentList(
    segments: List<Segment>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(segments) { segment ->
            Text(segment.name)
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
    LaunchedEffect(permissionRequest.allPermissionsGranted) {
        if (permissionRequest.allPermissionsGranted) {
            getLastLocation(context, onLocationUpdate)
        }
    }

    CtaButton(
        onClick = {
            if (!permissionRequest.allPermissionsGranted) {
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