package com.valbonet.wakeuplyapp.presentation.view

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.valbonet.wakeuplyapp.R
import com.valbonet.wakeuplyapp.data.connection.Data
import com.valbonet.wakeuplyapp.model.item.Item
import com.valbonet.wakeuplyapp.presentation.view.holder.ViewPagerHolder
import com.valbonet.wakeuplyapp.utils.UrlUtils
import com.valbonet.wakeuplyapp.utils.UtilsVideo
import kotlinx.android.synthetic.main.playvideo.view.*

class VideoViewPagerAdapter(val context: Context, val viewPager2: ViewPager2, val data: List<Item?>): RecyclerView.Adapter<ViewPagerHolder>() {

    var myViewHolders: ArrayList<ViewPagerHolder> = ArrayList()
    val TAG = "VideoViewPagerAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_viewpager, parent, false)
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {

        val item = data.get(position);
        loadNextVideo(holder, item, position)
        holder.bind(item!!)
        myViewHolders.add(position, holder)

    }

    override fun getItemCount()  = data.size

    fun play(position: Int) {
        if(myViewHolders.size > position) {
            val holder = myViewHolders.get(position)
            holder.play()
            pause(position)
        }
    }

    fun pause(position: Int){
        for (i in myViewHolders.indices) {
            if (i != position) {
                val holder: ViewPagerHolder = myViewHolders.get(i)
                holder.pause()
            }
        }

    }

    private fun loadNextVideo(holder: ViewPagerHolder, item: Item?, position: Int){
        val thread = Thread {
            val video = Data.getVideo(UrlUtils().createVideoURL(item))
            val vid = Uri.parse(video.videoURL)
            UtilsVideo.setVideoURI(holder.getVideoView(), vid, video.cookie)

        }
        thread.start()
    }

}