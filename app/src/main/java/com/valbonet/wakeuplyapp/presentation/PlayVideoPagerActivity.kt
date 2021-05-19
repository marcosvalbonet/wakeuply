package com.valbonet.wakeuplyapp.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.valbonet.wakeuplyapp.R
import com.valbonet.wakeuplyapp.model.item.Item
import com.valbonet.wakeuplyapp.presentation.view.VideoViewPagerAdapter
import com.valbonet.wakeuplyapp.utils.Utils
import kotlinx.android.synthetic.main.alarm_activity.*
import java.lang.reflect.Type


class PlayVideoPagerActivity : AppCompatActivity() {

    private var viewPager2: ViewPager2? = null
    private var pageAdapter: VideoViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playvideopageradapter)

        //Remove title bar
        supportActionBar!!.hide()

        viewPager2 = findViewById<ViewPager2>(R.id.viewPager2)
        viewPager2?.setOrientation(ViewPager2.ORIENTATION_VERTICAL)

        //TODO: pass de list
        val itemsJson: String? = intent.getStringExtra("data")
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<Item?>?>() {}.type
        val items: List<Item> = gson.fromJson(itemsJson, listType)

        pageAdapter = VideoViewPagerAdapter (this, viewPager2!!, items)
        viewPager2?.setAdapter(pageAdapter)
        viewPager2?.setOffscreenPageLimit(2)

        viewPager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pageAdapter?.play(position)
            }

        })
    }

    override fun onStart() {
        super.onStart()
        if (!Utils.isConnectingToInternet(this)) {
            Toast.makeText(applicationContext, R.string.connection_needed,
                    Toast.LENGTH_LONG).show()

            finish()
        }
    }

    override fun onDestroy() {
        viewPager2!!.adapter = null
        viewPager2 = null
        pageAdapter = null
        super.onDestroy()
    }
}