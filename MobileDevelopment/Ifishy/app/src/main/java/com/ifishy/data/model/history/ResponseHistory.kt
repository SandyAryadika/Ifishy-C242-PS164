package com.ifishy.data.model.history

import com.google.gson.annotations.SerializedName

data class ResponseHistory(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("confidence")
	val confidence: Float? = null,

	@field:SerializedName("fishImage")
	val fishImage: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null
)
