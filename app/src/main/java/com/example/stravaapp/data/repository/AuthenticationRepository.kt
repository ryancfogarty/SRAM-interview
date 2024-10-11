package com.example.stravaapp.data.repository

import com.example.stravaapp.data.api.OAuthService
import com.example.stravaapp.data.cache.AccessTokenCache
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val service: OAuthService
) {
    suspend fun authenticate(code: String) {
        AccessTokenCache.token = service.token(code = code).accessToken
    }
}