package com.valbonet.wakeuplyapp.model.item

import com.google.gson.annotations.SerializedName

data class Item (
        @SerializedName("id")
        val id: String? = null,

        @SerializedName("desc")
        val desc: String? = null,

        @SerializedName("createTime")
        val createTime: Int? = null,

        @SerializedName("video")
        val video: Video? = null,

        @SerializedName("author")
        val author: Author? = null,

        @SerializedName("music")
        val music: Music? = null,

        @SerializedName("stats")
        val stats: Stats? = null,

       // @SerializedName("duetInfo")
        //private DuetInfo duetInfo;
        @SerializedName("originalItem")
        val originalItem: Boolean? = null,

        @SerializedName("officalItem")
        val officalItem: Boolean? = null,

        @SerializedName("secret")
        val secret: Boolean? = null,

        @SerializedName("forFriend")
        val forFriend: Boolean? = null,

        @SerializedName("digged")
        val digged: Boolean? = null,

        @SerializedName("itemCommentStatus")
        val itemCommentStatus: Int? = null,

        @SerializedName("showNotPass")
        val showNotPass: Boolean? = null,

        @SerializedName("vl1")
        val vl1: Boolean? = null,

        @SerializedName("itemMute")
        val itemMute: Boolean? = null,

        @SerializedName("authorStats")
        val authorStats: AuthorStats? = null,

        @SerializedName("privateItem")
        val privateItem: Boolean? = null,

        @SerializedName("duetEnabled")
        val duetEnabled: Boolean? = null,

        @SerializedName("stitchEnabled")
        val stitchEnabled: Boolean? = null,

        @SerializedName("shareEnabled")
        val shareEnabled: Boolean? = null,

        @SerializedName("isAd")
        val isAd: Boolean? = null,
)