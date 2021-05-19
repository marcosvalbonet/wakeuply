package com.valbonet.wakeuplyapp.presentation.listener

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import com.google.gson.Gson
import com.valbonet.wakeuplyapp.Constants
import com.valbonet.wakeuplyapp.model.item.Item
import com.valbonet.wakeuplyapp.presentation.PlayUserActivity
import com.valbonet.wakeuplyapp.presentation.PlayVideoPagerActivity
import com.valbonet.wakeuplyapp.presentation.PlayVideoPagerAdapterActivity
import com.valbonet.wakeuplyapp.presentation.presenter.PlayUserPresenter
import com.valbonet.wakeuplyapp.presentation.view.VideoGridAdapter
import com.valbonet.wakeuplyapp.utils.UrlUtils


class GridOnVideoClickListener(val appContext: Activity): OnItemClickListener {


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        // Get the GridView selected/clicked item text
        if(parent?.adapter is VideoGridAdapter) {
            val adapter: VideoGridAdapter = parent.adapter as VideoGridAdapter
            var selectedItem: Item? = adapter.getItem(position)
//            if (appContext is PlayUserActivity){
//                val playUserView = appContext as PlayUserActivity
//                playUserView.playUserPresenter.onDestroy()
//            }

            //To Deprecate
            val myIntent = Intent(appContext, PlayVideoPagerAdapterActivity::class.java)
            //TODO: call to retrofit and get html correctly
//            val myIntent = Intent(appContext, PlayVideoPagerActivity::class.java)

            myIntent.putExtra(Constants.imgUser, selectedItem?.author?.avatarThumb)
            myIntent.putExtra(Constants.muserNickname, selectedItem?.author?.uniqueId)
            myIntent.putExtra(Constants.videoName, selectedItem?.desc)
            myIntent.putExtra(Constants.videoURLPage, UrlUtils().createVideoURL(selectedItem))
            myIntent.putExtra(Constants.videoURL, selectedItem?.video?.playAddr)
            myIntent.putExtra(Constants.muserURL, UrlUtils().createMuserURL(selectedItem))
            myIntent.putExtra(Constants.isNewUrlMuser, appContext.intent?.extras?.containsKey("isNewURL")
                    ?: false)
            if (appContext.intent.extras != null && appContext.intent.extras!!.containsKey(Constants.alarmID)) {
                myIntent.putExtra(Constants.alarmID, appContext.intent.extras!!.getLong(Constants.alarmID))
            }
            // Deprecated PlayVideoPagerAdapterActivity
            myIntent.putStringArrayListExtra(Constants.videoURLPages, getArrayVideoUrlPages(adapter.items!!, selectedItem))
            // PlayVideoPagerActivity
//            val gson = Gson()
//            myIntent.putExtra("data", gson.toJson(getVideoItems(selectedItem, adapter?.items!!)))
            myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            appContext.startActivity(myIntent)
        }
    }

    //Deprecated PlayVideoPagerAdapterActivity
    private fun getArrayVideoUrlPages(items: List<Item?>, item: Item?): java.util.ArrayList<String>? {
        val pageVideoUrl = java.util.ArrayList<String>()
        var isFirstUrlPageFound = false
        for (it in items) {
            isFirstUrlPageFound = isFirstUrlPageFound || it == item
            if (isFirstUrlPageFound) {
                pageVideoUrl.add(UrlUtils().createVideoURL(it))
            }
        }
        return pageVideoUrl
    }

    //New system PlayVideoPagerActivity
    fun getVideoItems(selectedItem: Item?, items: List<Item?>):ArrayList<Item?>{
        val videoItems = ArrayList<Item?>()
        var itemFinded = false
        items.forEach {
                itemFinded = itemFinded || selectedItem?.id == it?.id
                if (itemFinded) videoItems.add(it)
        }
        return videoItems
    }
}