package com.example.stravaapp.presentation

import com.example.stravaapp.data.repository.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var sut: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        sut = LoginViewModel(authenticationRepository)
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
        val viewActions = mutableListOf<ViewAction>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            sut.viewAction.toList(viewActions)
        }
        coEvery { authenticationRepository.authenticate(code) } just runs

        sut.authenticate(code)

        assertEquals(
            listOf(ViewAction.NavigateToExploreScreen),
            viewActions
        )
        assertEquals(
            State.Initialized,
            sut.uiState.value
        )
    }

    @Test
    fun givenAuthenticationFails_whenAuthenticate_thenDisplayErrorState() = runTest {
        val code = "code"
        val viewActions = mutableListOf<ViewAction>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            sut.viewAction.toList(viewActions)
        }
        coEvery { authenticationRepository.authenticate(code) } throws Exception()

        sut.authenticate(code)

        assertEquals(
            emptyList<ViewAction>(),
            viewActions
        )
        assertEquals(
            State.Error,
            sut.uiState.value
        )
    }
}