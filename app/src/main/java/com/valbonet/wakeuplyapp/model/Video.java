package com.valbonet.wakeuplyapp.model;

import android.os.Bundle;
import com.valbonet.wakeuplyapp.Constants;

public class Video {

    private String videoURLPage;
    private String videoURL;
    private String videoURLNoWatermark;
    private String videoName;
    private String musicName;
    private String cookie;

    public Video(String videoURLPage, String videoURL, String videoName, String cookie){

        this.videoURLPage = videoURLPage;
        this.videoURL = videoURL;
        this.videoURLNoWatermark = null;//Data.getUrlVideoNoWatermark(videoURL);
        this.videoName = videoName;
        this.cookie = cookie;

    }

    public Video(Bundle bundle){
        this.videoURLPage = bundle.getString(Constants.videoURLPage);
        this.videoURL = bundle.getString(Constants.videoURL);
        this.videoName = bundle.getString(Constants.videoName);
        //this.videoURLNoWatermark = Data.getUrlVideoNoWatermark(videoURL);


    }

    public String getVideoURLPage() {
        return videoURLPage;
    }

    public void setVideoURLPage(String videoURLPage) {
        this.videoURLPage = videoURLPage;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getVideoURLNoWatermark() {
        return videoURLNoWatermark;
    }

    public void setVideoURLNoWatermark(String videoURLNoWatermark) {
        this.videoURLNoWatermark = videoURLNoWatermark;
    }

    public String getCookie()  {
        return cookie;
    }
}
