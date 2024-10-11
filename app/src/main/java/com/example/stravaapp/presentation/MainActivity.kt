package com.example.stravaapp.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.stravaapp.presentation.screen.LoginScreen
import com.example.stravaapp.presentation.theme.StravaAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("AuthCode", extractAuthCode() ?: "null")

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
        if (queryParameterNames.contains("code")) {
            return data.getQueryParameter("code")
        }

        return null
    }

    private fun authorize() {
        val intentUri = Uri.parse("https://www.strava.com/oauth/mobile/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", "137314")
            .appendQueryParameter("redirect_uri", "com.example.stravaapp://localhost")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("approval_prompt", "auto")
            .appendQueryParameter("scope", "activity:write,read")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        startActivity(intent)
    }
}