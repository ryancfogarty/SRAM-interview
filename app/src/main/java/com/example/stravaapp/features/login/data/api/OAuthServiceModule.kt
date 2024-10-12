package com.example.stravaapp.features.login.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
        val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.strava.com/oauth/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OAuthService::class.java)
    }
}