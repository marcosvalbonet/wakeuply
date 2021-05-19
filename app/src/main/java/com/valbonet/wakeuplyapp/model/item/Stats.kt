package com.valbonet.wakeuplyapp.model.item

import com.google.gson.annotations.SerializedName

data class Stats (
        @SerializedName("diggCount")
        val diggCount: Int? = null,

        @SerializedName("shareCount")
        val shareCount: Int? = null,

        @SerializedName("commentCount")
        val commentCount: Int? = null,

        @SerializedName("playCount")
        val playCount: Int? = null,
)
