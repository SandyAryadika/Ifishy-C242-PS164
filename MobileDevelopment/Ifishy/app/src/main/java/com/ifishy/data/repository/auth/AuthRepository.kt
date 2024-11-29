package com.ifishy.data.repository.auth

import android.app.Application
import com.google.gson.Gson
import com.ifishy.R
import com.ifishy.api.ApiService
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.request.RegisterRequest
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.RegisterResponse
import com.ifishy.utils.ResponseState
import retrofit2.HttpException
import java.io.IOException

class AuthRepository private constructor(private val apiService: ApiService,private val context: Application) {

    suspend fun login(email: String, password: String): ResponseState<LoginResponse>{
        return try {
            val response = apiService.login(LoginRequest(email,password))
            ResponseState.Success(response)
        }catch (e : IOException){
            ResponseState.Error(context.getString(R.string.no_internet))
        }catch (e : HttpException){
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse,ErrorResponse::class.java)
            ResponseState.Error(errorMessage.message!!)
        }
    }

    suspend fun register(username:String,email: String,password: String,confirmPassword:String): ResponseState<RegisterResponse>{
        return try {
            val response = apiService.register(RegisterRequest(username,email,password,confirmPassword))
            ResponseState.Success(response)
        }catch (e: IOException){
            ResponseState.Error(context.getString(R.string.no_internet))
        }catch (e: HttpException){
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse,ErrorResponse::class.java)
            ResponseState.Error(errorMessage.message!!)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository?=null

        fun getInstance(apiService: ApiService,context: Application): AuthRepository{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: AuthRepository(apiService,context).also { INSTANCE = it }
            }
        }
    }
}