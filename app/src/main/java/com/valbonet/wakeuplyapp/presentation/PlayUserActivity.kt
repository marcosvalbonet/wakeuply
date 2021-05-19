package com.valbonet.wakeuplyapp.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.valbonet.wakeuplyapp.Constants
import com.valbonet.wakeuplyapp.R
import com.valbonet.wakeuplyapp.model.item.Author
import com.valbonet.wakeuplyapp.model.item.AuthorStats
import com.valbonet.wakeuplyapp.model.item.Item
import com.valbonet.wakeuplyapp.model.item.Tiktoker
import com.valbonet.wakeuplyapp.presentation.listener.GridOnVideoClickListener
import com.valbonet.wakeuplyapp.presentation.presenter.PlayUserPresenter
import com.valbonet.wakeuplyapp.presentation.view.VideoGridAdapter
import com.valbonet.wakeuplyapp.usecases.GetTiktokerUseCase
import com.valbonet.wakeuplyapp.utils.UrlUtils
import kotlinx.android.synthetic.main.playuser.*
import java.util.*

class PlayUserActivity : AppCompatActivity(), PlayUserPresenter.View {

    var playUserPresenter: PlayUserPresenter? = null
    var videoGridAdapter : VideoGridAdapter? = null

    var loadingMoreVideos: Boolean = false
    var videosCount = 20

    var nickUser : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.playuser)
        //setSupportActionBar(findViewById(R.id.toolbar))

        setTheme(R.style.MainAppThemeLight)


        val nameUser = intent.extras?.getString("nameUser")
        nickUser = intent.extras?.getString("nickUser")
        val userId = intent.extras?.getString("userId")
        val secUid = intent.extras?.getString("secUid")
        val urlUser = intent.extras?.getString("urlUser")
        val isNewURL = intent?.extras?.getBoolean("isNewURL") ?: false
//       val isNewUrlMuser = intent?.extras?.containsKey("isNewURL") ?: false

        if (isNewURL && nickUser == null) {
            nickUser = UrlUtils().getMuserNicknameFromURL(urlUser)
        }

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = nameUser


        playUserPresenter = PlayUserPresenter(this, userId, secUid, nickUser, GetTiktokerUseCase())
        playUserPresenter?.onCreate()

    }

    override fun onDestroy() {
        playUserPresenter?.onDestroy()
        playUserPresenter = null
        videoGridAdapter = null
        super.onDestroy()
    }

    override fun renderTiktoker(tiktoker: Tiktoker?) {
        if (tiktoker == null || tiktoker.items == null || tiktoker.items.size == 0) {
            //launchNoUserMessage()
            val urlUser = intent.extras?.getString("urlUser")
            goToPlayUserActivity(urlUser)
            return;
        }

        val items = ArrayList<Item>()
        tiktoker.items.let{ items.addAll(it)}

        val author : Author  = items.get(0).author!!
        val authorStats : AuthorStats = items.get(0).authorStats!!

        val glideUrl = GlideUrl(author.avatarThumb, LazyHeaders.Builder()
                .addHeader("User-Agent", Constants.userAgent).build())

        Glide.with(this)
                .load(glideUrl)
                .placeholder(R.drawable.wakeuply)
                .error(R.drawable.wakeuply)
                .into(userImage);

        nickname.text = "@"+author.uniqueId

        followersCounter.text = getCountRounded(authorStats.followerCount.toString())
        followingCounter.text = getCountRounded(authorStats.followingCount.toString())
        likesCounter.text = getCountRounded(authorStats.heart.toString())

        videoGridAdapter = VideoGridAdapter(this, items)
        gridview.adapter = videoGridAdapter

        gridview.onItemClickListener = GridOnVideoClickListener(this)

        gridview.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - visibleItemCount <= firstVisibleItem) {
                    //&& videoGridAdapter.count + ITEM_COUNT <= MAX_ITEM_COUNT
                    //TODO: Add footer or some progress view to show that items are loading
//                    if (!loadingMoreVideos) {
//                        playUserPresenter?.getMoreVideos(videosCount, videoGridAdapter?.count!!)
//                        loadingMoreVideos = true
//                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, state: Int) {
                //TODO: add some logic if needed, but no logic needed for this task
            }
        })

        userPane.visibility = View.VISIBLE
        exceptionPane.visibility = View.GONE

        updateTiktokImage(nickUser, author.avatarThumb)

    }

    override fun renderVideos(tiktoker: Tiktoker) {
        val items = ArrayList<Item>()
        items.addAll(tiktoker.items!!)
        videoGridAdapter?.setItems(items)
        loadingMoreVideos = false

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun launchNoUserMessage(){
        userPane.visibility = View.GONE
        exceptionPane.visibility = View.VISIBLE
    }

    fun getCountRounded(count: String?) : String {
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

    fun updateTiktokImage(nickname: String?, tiktokImage: String?){
        val bundleUserImg = intent?.extras?.getString("userImgURL")
        if (bundleUserImg == null ||
                !UrlUtils().compareImages(bundleUserImg, tiktokImage)){
            playUserPresenter?.updateTiktokerImg(nickname, tiktokImage)
        }
    }

    fun goToPlayUserActivity(url: String?) {
        val myIntent = Intent(applicationContext, PlayUserWebActivity::class.java)
        //        Intent myIntent = new Intent(activity.getApplicationContext(), PlayUserActivity.class);
        myIntent.putExtra("urlUser", url)
        //myIntent.putExtra("isNewURL", true)
        if (intent.extras != null && intent.extras!!.containsKey("alarmID")) {
            myIntent.putExtra("alarmID", intent.extras!!.getLong("alarmID"))
        }
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        finish();
        applicationContext.startActivity(myIntent)

    }
}