package com.example.stravaapp.common

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {
    val screen = MutableSharedFlow<Screen>(replay = 0)
}

enum class Screen {
    Login,
    Explore
}