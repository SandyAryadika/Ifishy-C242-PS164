package com.ifishy.data.model.comments

import com.google.gson.annotations.SerializedName

data class CommentsResponse(

	@field:SerializedName("comments")
	val comments: List<CommentsItem>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class CommentsItem(

	@field:SerializedName("replies")
	val replies: List<RepliesItem>? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("profilePicture")
	val profilePic:String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int?=0,

	@field:SerializedName("userLiked")
	val userLiked: Boolean=false,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("timeSinceCommented")
	val timeSinceCommented: String? = null
)
