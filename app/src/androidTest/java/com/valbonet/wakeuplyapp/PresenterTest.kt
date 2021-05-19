package com.valbonet.wakeuplyapp

import com.valbonet.wakeuplyapp.presentation.presenter.PlayUserPresenter
import com.valbonet.wakeuplyapp.usecases.GetTiktokerUseCase
import org.junit.Test
import java.util.regex.Pattern

class PresenterTest {

    @Test
    fun callGetRateApi_isCorrect(){
//        val playUserPresenter = PlayUserPresenter(null, GetTiktokerUseCase())
//        playUserPresenter.onCreate()
//
//        assert(value = true)
    }

    @Test
    fun getUserId(){

        val resHTML = "\"createTime\":1451423399,\"verified\":false,\"secUid\":\"MS4wLjABAAAAyvBfAROOnIhVBZP9K9ISGAPB5GxL04rwxKCbTxqZ7SQ\",\"ftc\":false"

//            String substring = resHTML.substring(resHTML.indexOf("\"userInfo\":{\"user\":{\"id\":")+25);
//            String userId = substring.substring(1, substring.indexOf("\",\""));

        //String regex = "<script id=\"__NEXT_DATA__\" type=\"application/json\" crossorigin=\"anonymous\">(.*)</script><script crossorigin=\"anonymous\" nomodule=";
        val regex = "\"id\":\"(.*)\",\""
        val patron = Pattern.compile(regex)
        val matcher = patron.matcher(resHTML)
        val value = matcher.find()

        assert(value.equals("MS4wLjABAAAAyvBfAROOnIhVBZP9K9ISGAPB5GxL04rwxKCbTxqZ7SQ"))
    }
}