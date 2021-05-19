package com.valbonet.wakeuplyapp.presentation.view.holder

import android.app.Activity
import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.valbonet.wakeuplyapp.Constants
import com.valbonet.wakeuplyapp.R
import com.valbonet.wakeuplyapp.model.item.Item
import com.valbonet.wakeuplyapp.model.Video
import com.valbonet.wakeuplyapp.presentation.listener.CreateAlarmOnClickListener
import com.valbonet.wakeuplyapp.presentation.listener.TiktokShareOnClickListener
import com.valbonet.wakeuplyapp.utils.UtilsVideo
import kotlinx.android.synthetic.main.item_viewpager.view.*
import kotlinx.android.synthetic.main.playuser.*
import kotlinx.android.synthetic.main.playvideo.view.*
import kotlinx.android.synthetic.main.playvideo.view.myVideo

data class ViewPagerHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Item) = with(itemView) {

        val activity : Activity = itemView.context as Activity
//        val extras = activity.intent.extras

        //myVideo
//        val vid = Uri.parse(video?.videoURL)
//        UtilsVideo.setVideoURI(myVideo, vid, video?.cookie)
//        var vidURL = item.video?.playAddr
//        vidURL = vidURL?.replace("%7C", "|")
//        vidURL = vidURL?.replace("%3D", "=")
//        val vid = Uri.parse(video.videoURL)
//        UtilsVideo.setVideoURI(myVideo, vid, UtilsVideo.createCookieVideo())

        myVideo.setOnCompletionListener { l->l.start() }
        myVideo.setOnPreparedListener{ l-> UtilsVideo.applyScale(activity, l, myVideo) }
        //myVideo.setOnErrorListener()

        //Image
        image_view_profile_pic.setOnClickListener { activity.finish() }
        val glideUrl = GlideUrl(item.author?.avatarThumb, LazyHeaders.Builder()
                .addHeader("User-Agent", Constants.userAgent).build())

        Glide.with(this)
                .load(glideUrl)
                .placeholder(R.drawable.wakeuply)
                .error(R.drawable.wakeuply)
                .into(image_view_profile_pic);

        //Muser name
        text_view_account_handle.text = item.author?.uniqueId

        //Tiktok title
        text_view_video_description.text = item.desc

        //Tiktok music
        text_view_music_title.text = item.music?.title

        //Tiktok share
        share.setOnClickListener(TiktokShareOnClickListener(activity, item))

        //Create alarm
        alarm_view.setOnClickListener(CreateAlarmOnClickListener(activity, item))
    }

    fun play() {
        itemView.myVideo.start()
    }

    fun pause() {
        itemView.myVideo.pause()
    }

    fun getVideoView(): VideoView?{
        return itemView.myVideo
    }

}