package com.ifishy.data.model.bookmark

import com.google.gson.annotations.SerializedName

data class BookmarkRequest(
    @field:SerializedName("itemId")
    val itemId: Int,
    @field:SerializedName("type")
    val type: String
)
