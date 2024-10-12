package com.example.stravaapp.presentation

import com.example.stravaapp.features.login.data.repository.AuthenticationRepository
import com.example.stravaapp.common.Navigator
import com.example.stravaapp.common.Screen
import com.example.stravaapp.features.login.presentation.LoginViewModel
import com.example.stravaapp.features.login.presentation.State
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
        Dispatchers.setMain(UnconfinedTestDispatcher())
        sut = LoginViewModel(
            authenticationRepository = authenticationRepository,
            navigator = navigator
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenAuthenticate_thenDisplayAuthenticatingState() = runTest {
        val uiStates = mutableListOf<State>()
        val code = "code"
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            sut.uiState.toList(uiStates)
        }
        coEvery { authenticationRepository.authenticate(code) } just runs

        sut.authenticate(code)

        assertEquals(
            State.Authenticating,
            uiStates[1],
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

        coVerify(exactly = 1) { screenFlow.emit(Screen.Explore) }
        assertEquals(
            State.Initialized,
            sut.uiState.value
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

        coVerify(exactly = 0) { screenFlow.emit(Screen.Explore) }
        assertEquals(
            State.Error,
            sut.uiState.value
        )
    }
}