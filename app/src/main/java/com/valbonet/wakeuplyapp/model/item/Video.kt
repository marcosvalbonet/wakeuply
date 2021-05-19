package com.valbonet.wakeuplyapp.model.item

import com.google.gson.annotations.SerializedName

data class Video (
        @SerializedName("id")
        val id: String? = null,

        @SerializedName("height")
        val height: Int? = null,

        @SerializedName("width")
        val width: Int? = null,

        @SerializedName("duration")
        val duration: Int? = null,

        @SerializedName("ratio")
        val ratio: String? = null,

        @SerializedName("cover")
        val cover: String? = null,

        @SerializedName("originCover")
        val originCover: String? = null,

        @SerializedName("dynamicCover")
        val dynamicCover: String? = null,

        @SerializedName("playAddr")
        val playAddr: String? = null,

        @SerializedName("downloadAddr")
        val downloadAddr: String? = null,

        @SerializedName("shareCover")
        val shareCover : List<String>? = null,

        @SerializedName("reflowCover")
        val reflowCover: String? = null,
)