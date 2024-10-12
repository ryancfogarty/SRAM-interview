package com.example.stravaapp.features.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stravaapp.R
import com.example.stravaapp.common.presentation.component.CtaButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onAuthorize: () -> Unit,
) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.login))
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
            when (state) {
                is State.Initialized -> {
                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.welcome_message),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }

                    CtaButton(
                        title = stringResource(R.string.connect_with_strava),
                        onClick = onAuthorize
                    )
                }

                is State.Authenticating -> {
                    CircularProgressIndicator()
                }

                is State.Error -> {
                    Error(onRetry = onAuthorize)
                }
            }
        }
    }
}

@Composable
fun Error(
    onRetry: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.authorization_error_message),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )

        CtaButton(
            title = stringResource(R.string.retry_connecting),
            onClick = onRetry,
        )
    }
}