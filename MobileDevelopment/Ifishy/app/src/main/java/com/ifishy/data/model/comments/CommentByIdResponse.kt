package com.ifishy.data.model.comments

import com.google.gson.annotations.SerializedName

data class CommentByIdResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("comment")
	val comment: Comment? = null
)

data class Comment(

	@field:SerializedName("profilePicture")
	val profilePicture: Any? = null,

	@field:SerializedName("replies")
	val replies: List<RepliesItem>?=null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("userLiked")
	val userLiked: Boolean?=null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("timeSinceCommented")
	val timeSinceCommented: String? = null
)

data class RepliesItem(

	@field:SerializedName("profilePicture")
	val profilePicture: Any? = null,

	@field:SerializedName("timeSinceReplied")
	val timeSinceReplied: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("userLiked")
	val userLiked: Boolean? = null,

	@field:SerializedName("username")
	val username: String? = null
)
