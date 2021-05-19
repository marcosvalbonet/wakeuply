package com.valbonet.wakeuplyapp.presentation.searcher.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.bumptech.glide.Glide
import com.valbonet.wakeuplyapp.R
import com.valbonet.wakeuplyapp.data.LoadImageTask
import com.valbonet.wakeuplyapp.model.search.Muser
import com.valbonet.wakeuplyapp.utils.Utils
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*


class MuserAdapter(
        context: Context,
        @LayoutRes val resource: Int,
        val musers: ArrayList<Muser?>
) : ArrayAdapter<Muser>(context, resource) {

    override fun getCount(): Int {
        return musers.size
    }

    override fun getItem(position: Int): Muser? {
        return musers[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(resource, parent, false)

        var muser = getItem(position)

        val avatar = rowView!!.findViewById<View>(R.id.iv_avatar) as ImageView
        val name = rowView?.findViewById<View>(R.id.tv_name) as TextView
        val nickuser = rowView?.findViewById<View>(R.id.tv_title) as TextView
        val moreInfo = rowView!!.findViewById<View>(R.id.tv_company) as TextView

        name.text = muser?.name
        nickuser.text = muser?.nickname
        moreInfo.text = ""

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

        val bytesImage: ByteArray =
            Base64.getDecoder().decode(muser?.avatarBlob)
            val `is`: InputStream = ByteArrayInputStream(bytesImage)
            val bmp = BitmapFactory.decodeStream(`is`)
            Glide.with(context)
                .load(bmp)
                .placeholder(R.drawable.wakeuply)
                .error(R.drawable.wakeuply)
                .into(avatar!!)
        } else {
            val loadImage = LoadImageTask(avatar, muser?.nickname)
            loadImage.execute(Utils.reviewURL(muser?.avatarMedium))
        }

        return rowView
    }

    fun addLeads(muserList: Collection<Muser?>?) {
        if (muserList != null) {
            musers.addAll(muserList)
        }
        notifyDataSetChanged()
    }

    fun refreshLeads(muserList: Collection<Muser?>?) {
        if (muserList != null) {
            musers.clear()
            musers.addAll(muserList)
        }
        notifyDataSetChanged()
    }
}