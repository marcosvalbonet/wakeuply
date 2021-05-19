package com.valbonet.wakeuplyapp.model.item

import com.google.gson.annotations.SerializedName

data class Author (
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("uniqueId")
    val uniqueId : String? = null,

    @SerializedName("nickname")
    val nickname : String? = null,

    @SerializedName("avatarThumb")
    val avatarThumb : String? = null,

    @SerializedName("avatarMedium")
    val avatarMedium : String? = null,

    @SerializedName("avatarLarger")
    val avatarLarger : String? = null,

    @SerializedName("signature")
    val signature : String? = null,

    @SerializedName("verified")
    val verified : Boolean? = null,

    @SerializedName("secUid")
    val secUid : String? = null,

    @SerializedName("secret")
    val secret : Boolean? = null,

    @SerializedName("ftc")
    val ftc : Boolean? = null,

    @SerializedName("relation")
    val relation : Int? = null,

    @SerializedName("openFavorite")
    val openFavorite : Boolean? = null,

    @SerializedName("commentSetting")
    val commentSetting : Int? = null,

    @SerializedName("duetSetting")
    val duetSetting : Int? = null,

    @SerializedName("stitchSetting")
    val stitchSetting : Int? = null,

    @SerializedName("privateAccount")
    val privateAccount : Boolean? = null
)