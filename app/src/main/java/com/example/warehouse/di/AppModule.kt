package com.example.warehouse.di

import com.example.warehouse.data.remote.ApiService
import com.example.warehouse.data.remote.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(
            Interceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Ocp-Apim-Subscription-Key", NetworkConfig.SUBSCRIPTION_KEY)
                    .build()
                chain.proceed(request)
            }
        )
        return builder.build()
    }
}

