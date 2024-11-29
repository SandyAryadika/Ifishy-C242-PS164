package com.ifishy.api

import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.request.RegisterRequest
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body userData: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body userData: RegisterRequest): RegisterResponse
}