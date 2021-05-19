package com.valbonet.wakeuplyapp.model.item

import com.google.gson.annotations.SerializedName

data class Music (
        @SerializedName("id")
        val id: String? = null,

        @SerializedName("title")
        val title: String? = null,

        @SerializedName("playUrl")
        val playUrl: String? = null,

        @SerializedName("coverThumb")
        val coverThumb: String? = null,

        @SerializedName("coverMedium")
        val coverMedium: String? = null,

        @SerializedName("coverLarge")
        val coverLarge: String? = null,

        @SerializedName("authorName")
        val authorName: String? = null,

        @SerializedName("original")
        val original: Boolean? = null,

        @SerializedName("duration")
        val duration: Integer? = null,

        @SerializedName("album")
        val album: String? = null,
)