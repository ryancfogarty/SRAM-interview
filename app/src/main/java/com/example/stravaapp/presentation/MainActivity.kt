package com.example.stravaapp.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.stravaapp.BuildConfig
import com.example.stravaapp.presentation.screen.LoginScreen
import com.example.stravaapp.presentation.theme.StravaAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractAuthCode()?.let(viewModel::authenticate)

        enableEdgeToEdge()
        setContent {
            StravaAppTheme {
                LoginScreen(onAuthorize = ::authorize)
            }
        }
    }

    private fun extractAuthCode(): String? {
        val data = intent.data ?: return null
        val queryParameterNames = data.queryParameterNames
        if (queryParameterNames.contains(AUTH_RESPONSE_CODE_KEY)) {
            return data.getQueryParameter(AUTH_RESPONSE_CODE_KEY)
        }

        return null
    }

    private fun authorize() {
        val intentUri = Uri.parse("https://www.strava.com/oauth/mobile/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", BuildConfig.STRAVA_CLIENT_ID)
            .appendQueryParameter("redirect_uri", "com.example.stravaapp://localhost")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("approval_prompt", "auto")
            .appendQueryParameter("scope", "activity:write,read")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        startActivity(intent)
    }

    companion object {
        private const val AUTH_RESPONSE_CODE_KEY = "code"
    }
}