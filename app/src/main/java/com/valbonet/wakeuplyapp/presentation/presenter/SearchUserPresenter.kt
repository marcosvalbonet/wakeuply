package com.valbonet.wakeuplyapp.presentation.presenter

import com.valbonet.wakeuplyapp.Constants
import com.valbonet.wakeuplyapp.model.search.Muser
import com.valbonet.wakeuplyapp.usecases.FindMusersByNicknameUseCase
import com.valbonet.wakeuplyapp.usecases.GetMusersUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchUserPresenter constructor(
        private var view: View?,
        private val getMuserListUserCase: GetMusersUseCase
){

    interface View {
        fun renderMusers(musers: List<Muser>)
        fun renderMusersFound(musers: List<Muser>, nickname:String?)
        fun goToPlayUserActivity(url: String?)
        fun showNotFoundMessage()
    }

    fun onCreate(sizeList: String?) {

        getMuserListUserCase.invoke("0", sizeList.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { s -> view?.renderMusers(s!!) },
                        { e -> println(e) },
                        { })

    }

    fun getMoreMusers(offset: String?, sizeList: String?){

        getMuserListUserCase.invoke(offset, sizeList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { s -> view?.renderMusers(s!!) },
                        { e -> println(e) },
                        { })

    }

    fun findMuserByNickname(nickname: String?){
        FindMusersByNicknameUseCase().invoke(nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ s -> view?.renderMusersFound(s!!, nickname)},
                        { e -> println(e) },
                        { }
                )

    }

    fun onDestroy() {
        view = null
    }

    fun isNicknameInList(musers : List<Muser>, nickname: String?) : Boolean {
        var isNicknameInList = false
        musers.forEach {
            isNicknameInList = isNicknameInList || it.nickname.equals(nickname)
        }
        return isNicknameInList
    }
}