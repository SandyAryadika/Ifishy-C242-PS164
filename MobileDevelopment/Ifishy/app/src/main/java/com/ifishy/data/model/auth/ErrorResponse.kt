package com.ifishy.data.model.auth

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
