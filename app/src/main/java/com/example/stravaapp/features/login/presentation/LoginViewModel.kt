package com.example.stravaapp.features.login.presentation

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.stravaapp.common.Navigator
import com.example.stravaapp.common.Screen
import com.example.stravaapp.features.login.data.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    @OptIn(SavedStateHandleSaveableApi::class)
    var uiState: State by savedStateHandle.saveable {
        mutableStateOf(State.Initialized)
    }
        private set

    fun authenticate(code: String) {
        uiState = State.Authenticating
        viewModelScope.launch {
            try {
                authenticationRepository.authenticate(code = code)

                uiState = State.Initialized
                navigator.screen.emit(Screen.Explore)

            } catch (t: Throwable) {
                uiState = State.Error
            }
        }
    }
}


interface State : Parcelable {
    @Parcelize
    object Initialized : State

    @Parcelize
    object Authenticating : State

    @Parcelize
    object Error : State
}