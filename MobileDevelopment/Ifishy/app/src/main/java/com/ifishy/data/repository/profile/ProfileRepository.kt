package com.ifishy.data.repository.profile

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.ifishy.R
import com.ifishy.api.ApiService
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.data.model.comments.MessageResponse
import com.ifishy.data.model.profile.request.EditProfileRequest
import com.ifishy.data.model.profile.response.ProfileResponse
import com.ifishy.data.model.profile.response.UpdatePhotoProfileResponse
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface ProfileRepository {
    suspend fun getProfile(token:String,email:String): ResponseState<ProfileResponse>
    suspend fun editProfile(token: String, userData: EditProfileRequest): SingleEvent<ResponseState<MessageResponse>>
    suspend fun updateProfile(token: String,image: MultipartBody.Part): SingleEvent<ResponseState<UpdatePhotoProfileResponse>>
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

    override suspend fun editProfile(
        token: String,
        userData: EditProfileRequest
    ): SingleEvent<ResponseState<MessageResponse>> {
        return try{
            val response = apiService.editProfile("Bearer $token",userData)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun updateProfile(
        token: String,
        image: MultipartBody.Part
    ): SingleEvent<ResponseState<UpdatePhotoProfileResponse>> {
        return try{
            val response = apiService.updateProfile("Bearer $token",image)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }
}

