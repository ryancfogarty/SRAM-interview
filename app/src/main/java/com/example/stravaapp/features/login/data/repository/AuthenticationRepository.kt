package com.example.stravaapp.features.login.data.repository

import com.example.stravaapp.features.login.data.api.OAuthService
import com.example.stravaapp.common.data.cache.AccessTokenCache
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val service: OAuthService
) {
    suspend fun authenticate(code: String) {
        AccessTokenCache.token = service.token(code = code).accessToken
    }
}