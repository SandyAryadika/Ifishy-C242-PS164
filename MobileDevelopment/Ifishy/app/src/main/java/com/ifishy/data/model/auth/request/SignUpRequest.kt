package com.ifishy.data.model.auth.request

import com.google.gson.annotations.SerializedName

data class SignUpRequest(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("confirmPassword")
	val confirmPassword: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
