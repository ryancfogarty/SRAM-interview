package com.example.stravaapp.features.explore.data.api

import com.example.stravaapp.common.data.AuthenticatedClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object SegmentServiceModule {
    @Provides
    fun provideSegmentService(
        @AuthenticatedClient authenticatedClient: OkHttpClient
    ): SegmentService {
        val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .client(authenticatedClient)
            .baseUrl("https://www.strava.com/api/v3/segments/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SegmentService::class.java)
    }
}