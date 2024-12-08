package com.ifishy.ml

import com.ifishy.data.model.classification.ClassificationResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MlService {

    @Multipart
    @POST("predict")
    suspend fun getClassificaton(@Part image:MultipartBody.Part): ClassificationResponse

}