package com.ifishy.ml

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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MlClient {

    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG){
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }else{
            setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(5,TimeUnit.MINUTES)
        .readTimeout(5,TimeUnit.MINUTES)
        .writeTimeout(5,TimeUnit.MINUTES)
        .build()

    @Provides
    @Singleton
    fun provideMlService() : MlService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_ML)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MlService::class.java)
    }



}