package com.ifishy.data.model.classification

import com.google.gson.annotations.SerializedName

data class ClassificationResponse(

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("confidence")
	val confidence: String? = null,

	@field:SerializedName("details")
	val details: Details? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Details(

	@field:SerializedName("Validasi")
	val validasi: String? = null,

	@field:SerializedName("Rekomendasi Pengobatan")
	val rekomendasiPengobatan: String? = null,

	@field:SerializedName("Penyebab")
	val penyebab: String? = null
)
