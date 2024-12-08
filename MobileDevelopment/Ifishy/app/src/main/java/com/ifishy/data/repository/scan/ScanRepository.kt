package com.ifishy.data.repository.scan

import android.app.Application
import com.google.gson.Gson
import com.ifishy.R
import com.ifishy.data.model.auth.ErrorResponse
import com.ifishy.data.model.classification.ClassificationResponse
import com.ifishy.ml.MlService
import com.ifishy.utils.ResponseState
import com.ifishy.utils.SingleEvent
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface ScanRepository {
    suspend fun predict(image:MultipartBody.Part): SingleEvent<ResponseState<ClassificationResponse>>
}

class ScanRepositoryImpl @Inject constructor(private val mlService: MlService,private val context: Application):ScanRepository{
    override suspend fun predict(image: MultipartBody.Part): SingleEvent<ResponseState<ClassificationResponse>> {
        return try {
            val response = mlService.getClassificaton(image)
            SingleEvent(ResponseState.Success(response))
        }catch (e: IOException) {
            SingleEvent(ResponseState.Error(context.getString(R.string.no_internet)))
        } catch (e: HttpException) {
            SingleEvent(ResponseState.Error(e.response()?.errorBody().toString()))
        }catch (e: IllegalStateException){
            SingleEvent(ResponseState.Error(context.getString(R.string.internal_server_error)))
        }

    }

}
