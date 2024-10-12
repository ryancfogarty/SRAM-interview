package com.example.stravaapp.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stravaapp.features.login.data.repository.AuthenticationRepository
import com.example.stravaapp.common.Navigator
import com.example.stravaapp.common.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val _uiState = MutableStateFlow<State>(State.Initialized)
    val uiState: StateFlow<State> = _uiState

    fun authenticate(code: String) {
        _uiState.value = State.Authenticating
        viewModelScope.launch {
            try {
                authenticationRepository.authenticate(code = code)

                _uiState.value = State.Initialized
                navigator.screen.emit(Screen.Explore)

            } catch (t: Throwable) {
                _uiState.value = State.Error
            }
        }
    }
}

interface State {
    object Initialized : State
    object Authenticating : State
    object Error : State
}