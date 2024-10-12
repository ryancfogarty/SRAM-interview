package com.example.stravaapp.features.login.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.stravaapp.common.Navigator
import com.example.stravaapp.common.Screen
import com.example.stravaapp.features.login.data.repository.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val authenticationRepository: AuthenticationRepository = mockk()
    private val navigator: Navigator = mockk()
    private lateinit var sut: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        sut = LoginViewModel(
            authenticationRepository = authenticationRepository,
            navigator = navigator,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenAuthenticate_thenDisplayAuthenticatingState() = runTest {
        val code = "code"
        coEvery { authenticationRepository.authenticate(code) } just runs

        sut.authenticate(code)

        assertEquals(
            State.Authenticating,
            sut.uiState,
        )
    }

    @Test
    fun givenAuthenticationSucceeds_whenAuthenticate_thenNavigateToExploreScreen() = runTest {
        val code = "code"
        val screenFlow: MutableSharedFlow<Screen> = mockk()
        coEvery { navigator.screen } returns screenFlow
        coEvery { screenFlow.emit(Screen.Explore) } just runs
        coEvery { authenticationRepository.authenticate(code) } just runs

        sut.authenticate(code)
        advanceUntilIdle()

        coVerify(exactly = 1) { screenFlow.emit(Screen.Explore) }
        assertEquals(
            State.Initialized,
            sut.uiState
        )
    }

    @Test
    fun givenAuthenticationFails_whenAuthenticate_thenDisplayErrorState() = runTest {
        val code = "code"
        val screenFlow: MutableSharedFlow<Screen> = mockk()
        coEvery { navigator.screen } returns screenFlow
        coEvery { screenFlow.emit(Screen.Explore) } just runs
        coEvery { authenticationRepository.authenticate(code) } throws Exception()

        sut.authenticate(code)
        advanceUntilIdle()

        coVerify(exactly = 0) { screenFlow.emit(Screen.Explore) }
        assertEquals(
            State.Error,
            sut.uiState
        )
    }
}