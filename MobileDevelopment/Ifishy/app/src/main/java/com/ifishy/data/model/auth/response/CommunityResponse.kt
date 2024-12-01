package com.ifishy.data.model.auth.response

import com.google.gson.annotations.SerializedName

data class CommunityResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
