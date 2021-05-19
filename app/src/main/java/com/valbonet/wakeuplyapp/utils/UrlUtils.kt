package com.valbonet.wakeuplyapp.utils

import com.valbonet.wakeuplyapp.Constants
import com.valbonet.wakeuplyapp.model.item.Item

class UrlUtils {

    fun createVideoURL(item: Item?):String{
        return Constants.tiktokURL + "@" + item?.author?.uniqueId + "/video/" + item?.video?.id
    }

    fun createMuserURL(item: Item?):String{
        return Constants.tiktokURL + "@" + item?.author?.uniqueId
    }

    fun createMuserURL(uniqueId: String?):String{
        return Constants.tiktokURL + uniqueId
    }

    fun getMuserNicknameFromURL(url: String?):String{
        var paths = url?.split("/")
        return paths.let{it!!.get(1)}
    }

    fun compareImages(bundleUserImg: String, tiktokImage: String?): Boolean {

        var image = bundleUserImg.split("?")
        var tImage = tiktokImage?.split("?")
        return image?.get(0).equals(tImage?.get(0))
    }
}