package com.ifishy.data.repository.profile

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.ifishy.R
import com.ifishy.api.ApiService
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.data.model.profile.response.ProfileResponse
import com.ifishy.utils.ResponseState
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface ProfileRepository {
    suspend fun getProfile(token:String,email:String): ResponseState<ProfileResponse>
}

class ProfileRepositoryImpl @Inject constructor(private val apiService: ApiService ,@ApplicationContext private val context: Application) : ProfileRepository{
    override suspend fun getProfile(token: String,email: String): ResponseState<ProfileResponse> {
        return try {
            val response = apiService.getProfile("Bearer $token",email)
            ResponseState.Success(response)
        }catch (e: IOException) {
            ResponseState.Error(context.getString(R.string.no_internet))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            ResponseState.Error(errorMessage.message!!)
        }catch (e: IllegalStateException){
            ResponseState.Error(context.getString(R.string.internal_server_error))
        }
    }
}

