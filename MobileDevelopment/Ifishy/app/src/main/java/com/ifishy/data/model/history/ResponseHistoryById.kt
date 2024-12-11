package com.ifishy.data.model.history

import com.google.gson.annotations.SerializedName

data class ResponseHistoryById(

	@field:SerializedName("data")
	val data: List<DataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItem(

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

	@field:SerializedName("scanTimestamp")
	val scanTimestamp: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("validation")
	val validation: Any? = null
)
