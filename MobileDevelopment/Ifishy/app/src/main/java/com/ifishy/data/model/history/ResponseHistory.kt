package com.ifishy.data.model.history

import com.google.gson.annotations.SerializedName

data class ResponseHistory(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("confidence")
	val confidence: Float? = null,

	@field:SerializedName("fishImage")
	val fishImage: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("validation")
	val validation: String? = null
)
