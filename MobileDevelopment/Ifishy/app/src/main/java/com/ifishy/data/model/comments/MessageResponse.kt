package com.ifishy.data.model.comments

import com.google.gson.annotations.SerializedName

data class MessageResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
