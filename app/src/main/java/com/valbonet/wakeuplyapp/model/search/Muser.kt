package com.valbonet.wakeuplyapp.model.search

import com.google.gson.annotations.SerializedName

class Muser (
        @SerializedName("nickname")
        val nickname : String? = null,

        @SerializedName("name")
        val name : String? = null,

        @SerializedName("userId")
        val userId : String? = null,

        @SerializedName("secUid")
        val secUid : String? = null,

        @SerializedName("avatarMedium")
        val avatarMedium : String? = null,

        @SerializedName("avatarBlob")
        val avatarBlob : String? = null
)