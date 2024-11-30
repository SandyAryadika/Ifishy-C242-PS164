package com.ifishy.api.adwd

import com.ifishy.BuildConfig
import com.ifishy.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiClient {

    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG){
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }else{
            setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    @Provides
    @Singleton
    fun provideApiService() : ApiService{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }



}