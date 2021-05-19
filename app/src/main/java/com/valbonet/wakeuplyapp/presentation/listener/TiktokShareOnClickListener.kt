package com.valbonet.wakeuplyapp.presentation.listener

import android.app.Activity
import android.content.Intent
import android.view.View
import com.valbonet.wakeuplyapp.Constants
import com.valbonet.wakeuplyapp.model.Video
import com.valbonet.wakeuplyapp.model.item.Item

class TiktokShareOnClickListener(val appContext: Activity, val item: Item?) : View.OnClickListener {
    override fun onClick(v: View?) {

        //https://wakeuply.page.link/?link=&apn=com.valbonet.wakeuplyapp
        val dynamicLink = "https://wakeuply.page.link/?link=" +
                createVideoURL(item) +
                "&apn=com.valbonet.wakeuplyapp"

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Compartir")
        i.putExtra(Intent.EXTRA_TEXT, dynamicLink)
        appContext.startActivity(Intent.createChooser(i, "Compartir Tiktok"))
    }

    fun createVideoURL(item: Item?):String{
        return Constants.tiktokURL + "@" + item?.author?.uniqueId + "/video/" + item?.video?.id
    }
}