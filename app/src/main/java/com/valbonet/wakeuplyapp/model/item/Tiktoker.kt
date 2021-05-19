package com.valbonet.wakeuplyapp.model.item

import com.google.gson.annotations.SerializedName

data class Tiktoker (

    @SerializedName("statusCode")
    val statusCode: Int? = null,

    @SerializedName("itemList")
    val items: List<Item>? = null,

    @SerializedName("hasMore")
    val hasMore: Boolean? = null,

    @SerializedName("maxCursor")
    val maxCursor: String? = null,

    @SerializedName("minCursor")
    private val minCursor: String? = null
)