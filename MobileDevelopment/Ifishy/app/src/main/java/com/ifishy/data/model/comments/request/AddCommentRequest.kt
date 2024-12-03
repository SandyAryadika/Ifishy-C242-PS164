package com.ifishy.data.model.comments.request

import com.google.gson.annotations.SerializedName

class AddCommentRequest(

    @field:SerializedName("content")
    val content :String
)