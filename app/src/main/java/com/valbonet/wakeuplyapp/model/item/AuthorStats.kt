package com.valbonet.wakeuplyapp.model.item

import com.google.gson.annotations.SerializedName

data class AuthorStats (

    @SerializedName("followingCount")
    val followingCount: String? = null,

    @SerializedName("followerCount")
    val followerCount: String? = null,

    @SerializedName("heartCount")
    val heartCount: String? = null,

    @SerializedName("videoCount")
    val videoCount: String? = null,

    @SerializedName("diggCount")
    val diggCount: String? = null,

    @SerializedName("heart")
    val heart : String? = null

)