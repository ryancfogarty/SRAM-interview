package com.example.stravaapp.common.data

import com.example.stravaapp.common.data.cache.AccessTokenCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object OkHttpClientModule {

    @AuthenticatedClient
    @Provides
    fun provideAuthenticatedClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = AccessTokenCache.token
                if (token != null) {
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                } else throw UnauthorizedException()
            }
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient

class UnauthorizedException: Exception()