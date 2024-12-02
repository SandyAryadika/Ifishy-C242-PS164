package com.ifishy.data.model.profile.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("profile")
	val profile: Profile? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Profile(

	@field:SerializedName("profile_photo")
	val profilePhoto: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
