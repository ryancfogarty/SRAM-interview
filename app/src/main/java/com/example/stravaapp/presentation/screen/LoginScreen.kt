package com.example.stravaapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stravaapp.R
import com.example.stravaapp.presentation.LoginViewModel
import com.example.stravaapp.presentation.State


@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onAuthorize: () -> Unit,
) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                    AuthorizeButton(onAuthorize = onAuthorize)
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
fun AuthorizeButton(
    onAuthorize: () -> Unit
) {
    TextButton(onClick = onAuthorize) {
        Text(
            text = stringResource(R.string.authorize_with_strava),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun Error(
    onRetry: () -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.authorization_error_message)
        )

        TextButton(onClick = onRetry) {
            Text(
                text = stringResource(R.string.retry_authorization),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}