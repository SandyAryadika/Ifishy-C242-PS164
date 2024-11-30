package com.ifishy.data.repository.auth

import android.app.Application
import com.google.gson.Gson
import com.ifishy.R
import com.ifishy.api.ApiService
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.data.model.auth.request.LoginRequest
import com.ifishy.data.model.auth.request.SignUpRequest
import com.ifishy.data.model.auth.response.LoginResponse
import com.ifishy.data.model.auth.response.SignUpResponse
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(userData: LoginRequest): SingleEvent<ResponseState<LoginResponse>>
    suspend fun signUp(userData: SignUpRequest): SingleEvent<ResponseState<SignUpResponse>>
}

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Application
) : AuthRepository {

    override suspend fun login(userData: LoginRequest): SingleEvent<ResponseState<LoginResponse>> {
        return try {
            val response = apiService.login(userData)
            SingleEvent(ResponseState.Success(response))
        } catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun signUp(userData: SignUpRequest): SingleEvent<ResponseState<SignUpResponse>> {
        return try {
            val response = apiService.register(userData)
            SingleEvent(ResponseState.Success(response))
        } catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        } catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }
}
