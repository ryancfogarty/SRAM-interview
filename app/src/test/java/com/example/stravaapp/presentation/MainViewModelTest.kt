package com.example.stravaapp.presentation

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    private val sut = MainViewModel()

    @Before
    fun setup() {

    }

    @Test
    fun givenValidAuthResponse_whenExtractAuthCode_thenReturnsAuthCode() {
        val code = "123421"
        val data = Uri.Builder()
            .appendQueryParameter("code", code)
            .build()

        assertEquals(code, sut.extractAuthCode(data))
    }
}