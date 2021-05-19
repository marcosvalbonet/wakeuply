package com.valbonet.wakeuplyapp.presentation.view

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.valbonet.wakeuplyapp.Constants
import com.valbonet.wakeuplyapp.R
import com.valbonet.wakeuplyapp.model.item.Item
import java.util.ArrayList

class VideoGridAdapter(val context: Context, val items: ArrayList<Item>?) : BaseAdapter() {

    override fun getCount(): Int {
        return items?.size!!
    }

    override fun getItem(position: Int): Item? {
        return items?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return items?.get(position)?.id!!.toLong()
    }

    fun setItems(moreItems:ArrayList<Item>){
        items?.clear()
        items?.addAll(moreItems)
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridItem = inflater.inflate(R.layout.grid_item, viewGroup, false)

        val item : Item? = getItem(position);

        val imageVideo : ImageView = gridItem.findViewById(R.id.image_video)
        val numberViews : TextView = gridItem.findViewById(R.id.number_views)

        val glideUrl = GlideUrl(
                item?.video?.originCover, LazyHeaders.Builder()
                .addHeader("User-Agent", Constants.userAgent)
                .build())

        Glide.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.wakeuply)
                .error(R.drawable.wakeuply)
                .into(imageVideo);

        numberViews.text = getCountRounded(item?.stats?.playCount.toString())


        return gridItem!!
    }


    fun getCountRounded(count:String?) : String {
        var countRounded: String

        when (count?.length) {
            4 -> countRounded = count[0] + "," + count[1] + count[2] + "K"
            5 -> countRounded = count[0] + "" + count[1] + "," + count[2] + "K"
            6 -> countRounded = count[0] + "" + count[1] + count[2] + "K"
            7 -> countRounded = count[0] + "," + count[1] + count[2] + "M"
            8 -> countRounded = count[0] + "" + count[1] + "," + count[2] + "M"
            9 -> countRounded = count[0] + "" + count[1] + count[2] + "M"
            10 -> countRounded = count[0] + "," + count[1] + count[2] + "B"
            11 -> countRounded = count[0] + "" + count[1] + "," + count[2] + "B"
            12 -> countRounded = count[0] + "" + count[1] + count[2] + "B"
            13 -> countRounded = count[0] + "," + count[1] + count[2] + "MM"
            else -> { // Note the block
                countRounded = count.toString()
            }
        }

        return countRounded
    }

}