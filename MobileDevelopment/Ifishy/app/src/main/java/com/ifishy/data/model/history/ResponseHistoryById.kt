package com.ifishy.data.model.history

import com.google.gson.annotations.SerializedName

data class ResponseHistoryById(

	@field:SerializedName("data")
	val data: List<DataItem>? = null
)

data class DataItem(

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("confidence")
	val confidence: Float? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("validation")
	val validation: String? = null,

	@field:SerializedName("fish_image")
	val fishImage: String? = null,

	@field:SerializedName("scan_timestamp")
	val scanTimestamp: String? = null
)
