package com.ifishy.data.repository.history

import android.app.Application
import com.google.gson.Gson
import com.ifishy.api.ApiService
import com.ifishy.R
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.data.model.history.ResponseHistory
import com.ifishy.data.model.history.ResponseHistoryById
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface HistoryRepository{
    suspend fun saveScanHistory(fishImage: MultipartBody.Part,
                                userId: RequestBody,
                                disease: RequestBody,
                                confidence: RequestBody
    ): SingleEvent<ResponseState<ResponseHistory>>

    suspend fun getScanHistory(userId: Int): ResponseState<ResponseHistoryById>
}
class HistoryRepositoryImpl @Inject constructor(private val apiService: ApiService, @ApplicationContext private val context: Application) : HistoryRepository {

    override suspend fun saveScanHistory(
        fishImage: MultipartBody.Part,
        userId: RequestBody,
        disease: RequestBody,
        confidence: RequestBody
    ): SingleEvent<ResponseState<ResponseHistory>>{
        return try {
            val response = apiService.saveScanHistory(fishImage, userId, disease, confidence)
            SingleEvent(ResponseState.Success(response))
        } catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            SingleEvent(ResponseState.Error(errorMessage.message!!))
        } catch (e: IllegalStateException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }
    }

    override suspend fun getScanHistory(userId: Int): ResponseState<ResponseHistoryById> {
        return try {
            val response = apiService.getScanHistory(userId)
            ResponseState.Success(response)
        } catch (e: IOException) {
            ResponseState.Error(context.getString(R.string.no_internet))
        } catch (e: HttpException) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java)
            ResponseState.Error(errorMessage.message!!)
        } catch (e: IllegalStateException) {
           ResponseState.Error(context.getString(R.string.internal_server_error))
        }
    }
}