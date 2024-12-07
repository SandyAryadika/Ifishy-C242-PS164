package com.ifishy.data.model.bookmark

import com.google.gson.annotations.SerializedName

data class BookmarkResponse(

	@field:SerializedName("bookmarks")
	val bookmarks: List<BookmarksItem>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class BookmarksItem(

	@field:SerializedName("item_id")
	val itemId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)
