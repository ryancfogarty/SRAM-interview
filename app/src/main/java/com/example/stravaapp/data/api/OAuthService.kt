package com.example.stravaapp.data.api

import com.example.stravaapp.BuildConfig
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Query


interface OAuthService {
    @POST("token")
    suspend fun token(
        @Query("client_id") clientId: String = BuildConfig.STRAVA_CLIENT_ID,
        @Query("client_secret") clientSecret: String = BuildConfig.STRAVA_CLIENT_SECRET,
        @Query("code") code: String,
        @Query("grant_type") grantType: String = "authorization_code",
    ): TokenResponseDto
}

class TokenResponseDto(
    @Field("access_token") val accessToken: String,
    @Field("refresh_token") val refreshToken: String,
)