package com.ifishy.data.model.profile.request

import com.google.gson.annotations.SerializedName

data class EditProfileRequest(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("newPassword")
    val newPassword: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null
)

