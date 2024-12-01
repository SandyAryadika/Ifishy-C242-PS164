package com.ifishy.api

import com.ifishy.data.community.response.CommuntyResponse
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.request.SignUpRequest
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body userData: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body userData: SignUpRequest): SignUpResponse

    @GET("api/auth/community/posts")
    suspend fun getPosts(@Header("Authorization") token: String): CommuntyResponse
}