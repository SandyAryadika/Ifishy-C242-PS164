package com.ifishy.api.adwd

import com.ifishy.BuildConfig
import com.ifishy.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

}