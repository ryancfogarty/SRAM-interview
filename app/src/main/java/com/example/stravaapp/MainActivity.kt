package com.example.stravaapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stravaapp.common.Navigator
import com.example.stravaapp.common.Screen
import com.example.stravaapp.features.explore.ExploreScreen
import com.example.stravaapp.features.login.presentation.LoginScreen
import com.example.stravaapp.features.login.presentation.theme.StravaAppTheme
import com.example.stravaapp.features.login.presentation.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractAuthCode()?.let(viewModel::authenticate)

        enableEdgeToEdge()
        setContent {
            StravaAppTheme {
                MainNavHost()
            }
        }
    }

    @Composable
    private fun MainNavHost() {
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            navigator.screen.onEach { screen ->
                navController.navigate(screen.name)
            }.launchIn(this)
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Login.name
        ) {
            composable(Screen.Login.name) {
                LoginScreen(
                    viewModel = viewModel,
                    onAuthorize = ::authorize
                )
            }
            composable(Screen.Explore.name) {
                ExploreScreen()
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