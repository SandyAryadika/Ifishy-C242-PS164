package com.ifishy.data.model.community.response

import com.google.gson.annotations.SerializedName
import com.ifishy.data.model.community.response.CommentsItem

data class CommunityResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("posts")
	val posts: List<PostsItem>? = null
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

data class PostsItem(

	@field:SerializedName("shareCount")
	val shareCount: Int? = null,

	@field:SerializedName("comments")
	val comments: List<Any?>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("image_url")
	val imageUrl: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("voteCount")
	val voteCount: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("timeSincePosted")
	val timeSincePosted: String? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
