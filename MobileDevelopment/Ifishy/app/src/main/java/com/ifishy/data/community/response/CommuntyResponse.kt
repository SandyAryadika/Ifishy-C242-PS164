package com.ifishy.data.community.response

import com.google.gson.annotations.SerializedName

data class CommuntyResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("posts")
	val posts: List<PostsItem>? = null
)

data class PostsItem(

	@field:SerializedName("comments")
	val comments: List<CommentsItem>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("timeSincePosted")
	val timeSincePosted: String? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class CommentsItem(

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("timeSinceCommented")
	val timeSinceCommented: String? = null
)
