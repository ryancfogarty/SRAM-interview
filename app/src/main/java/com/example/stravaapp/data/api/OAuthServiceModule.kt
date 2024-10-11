package com.example.stravaapp.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object OAuthServiceModule {
    @Provides
    fun provideOAuthService(): OAuthService {
        return Retrofit.Builder()
            .baseUrl("https://www.strava.com/oauth")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(OAuthService::class.java)
    }
}