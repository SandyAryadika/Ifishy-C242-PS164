package com.ifishy.data.model.classification

import com.google.gson.annotations.SerializedName

data class ClassificationResponse(

	@field:SerializedName("disease")
	val disease: String,

	@field:SerializedName("confidence")
	val confidence: String,

	@field:SerializedName("details")
	val details: Details,

	@field:SerializedName("message")
	val message: String,
)

data class Details(

	@field:SerializedName("Validasi")
	val validasi: String,

	@field:SerializedName("Rekomendasi Pengobatan")
	val rekomendasiPengobatan: String,

	@field:SerializedName("Penyebab")
	val penyebab: String
)
