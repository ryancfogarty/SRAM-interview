package com.example.stravaapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stravaapp.data.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<State>(State.Initialized)
    val uiState: StateFlow<State> = _uiState

    private val _viewAction = MutableSharedFlow<ViewAction>(replay = 0)
    val viewAction: SharedFlow<ViewAction> = _viewAction

    fun authenticate(code: String) {
        _uiState.value = State.Authenticating
        viewModelScope.launch {
            try {
                authenticationRepository.authenticate(code = code)

                _uiState.value = State.Initialized
                _viewAction.emit(ViewAction.NavigateToExploreScreen)

            } catch (_: Throwable) {
                _uiState.value = State.Error
            }
        }
    }
}

interface State {
    object Initialized: State
    object Authenticating: State
    object Error: State
}

sealed interface ViewAction {
    data object NavigateToExploreScreen: ViewAction
}