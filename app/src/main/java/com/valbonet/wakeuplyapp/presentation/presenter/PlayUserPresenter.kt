package com.valbonet.wakeuplyapp.presentation.presenter

import android.app.Activity
import android.util.Log
import com.valbonet.wakeuplyapp.data.DataRepository
import com.valbonet.wakeuplyapp.model.item.Tiktoker
import com.valbonet.wakeuplyapp.model.search.Muser
import com.valbonet.wakeuplyapp.usecases.*
import com.valbonet.wakeuplyapp.utils.UrlUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutionException

class PlayUserPresenter constructor(
        private var view: View?,
        private var userId: String?,
        private var secUid: String?,
        private var nickname: String?,
        private val getTiktokerUserCase: GetTiktokerUseCase
){
    val TAG = this.javaClass.canonicalName

    interface View {
        fun renderTiktoker(tiktoker: Tiktoker?)
        fun renderVideos(tiktoker: Tiktoker)
    }

    fun onCreate() {

        val context = view as Activity
        val isNewURL = context.intent?.extras?.getBoolean("isNewURL") ?: false

        if (userId == null || userId!!.isEmpty() || secUid == null){
            getUserIdSecUid(nickname)
        }

        if(userId == null || secUid == null){
            view?.renderTiktoker(null)
        }else {

            getTiktokerUserCase.invoke(userId, secUid, 20, 0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { s ->  view?.renderTiktoker(s!!)
                                    if (isNewURL){ addNewTiktokMuser(s)}
                            },
                            { e -> println(e) },
                            { })
        }
    }

    fun getMoreVideos(offset: Int, sizeList: Int){

        getTiktokerUserCase.invoke(userId, secUid, offset, sizeList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { s -> s.let { view?.renderVideos(s) } },
                        { e -> println(e) },
                        { })

    }


    fun onDestroy() {
        view = null
    }

    private fun getUserIdSecUid(nickUser: String?) {
        val getUserIdUseCase = GetUserIdUseCase()
        try {
            val response = getUserIdUseCase.execute(nickUser).get() ?: return
            val ids = response.split(";".toRegex())
            if (ids.size > 1) {
                val userIdNew = ids.get(0)
                val secUidNew = ids.get(1)

                if (userId == null || userId.isNullOrEmpty()) {
                    updateTiktokerUserId(nickUser, userIdNew)
                    userId = userIdNew
                }
                if (secUid == null || secUid.isNullOrEmpty()) {
                    updateTiktokerSecUid(nickUser, secUidNew)
                    secUid = secUidNew
                }
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun addNewTiktokMuser (tiktoker: Tiktoker?){
        val author = tiktoker?.items?.get(0)?.author

        val muser: Muser = Muser(author?.uniqueId,
                author?.nickname,
                author?.id,
                author?.secUid,
                author?.avatarMedium,
                null
        )
        AddNewMuserUseCase().invoke(muser)
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        Log.d(TAG, response.message())
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
    }

    fun updateTiktokerUserId(nickname: String?, userId: String?){
        UpdateUserIdUseCase().invoke(nickname, userId)
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        Log.d(TAG, response.message())
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
    }

    fun updateTiktokerSecUid(nickname: String?, secUid: String?){
        UpdateSecUidUseCase().invoke(nickname, secUid)
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        Log.d(TAG, response.message())
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
    }

    fun updateTiktokerImg(nickname: String?, imgSrc: String?){
        val dataRepostory = DataRepository()
        val callback = dataRepostory.updateTiktokerImg(nickname, imgSrc)
        callback.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, response.message())
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun updateTiktokerName(){
//        Data.updateTiktokMuserName(usrNicknameValue, usrNameValue)
    }

}