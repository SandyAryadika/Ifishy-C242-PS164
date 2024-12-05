package com.ifishy.data.model.article

import com.google.gson.annotations.SerializedName

data class ArticleByIdResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("author")
	val author: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("cover_image")
	val coverImage: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("published_at")
	val publishedAt: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
