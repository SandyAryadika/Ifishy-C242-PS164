package com.ifishy.data.model.profile.response

import com.google.gson.annotations.SerializedName

data class UpdatePhotoProfileResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("imageUrl")
    val imageUrl: String?=null
)
