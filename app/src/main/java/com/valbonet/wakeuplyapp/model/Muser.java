package com.valbonet.wakeuplyapp.model;

import android.os.Bundle;

import com.valbonet.wakeuplyapp.Constants;

import java.util.ArrayList;

public class Muser {

    private String nickname;
    private String name;
    private String img;
    private String muserURL;
    private boolean isNew;

    private ArrayList playList;

    public Muser(){
    }

    public Muser(String nickname, String name, String img, String muserURL,
                 boolean isNew){
        this.nickname = nickname;
        this.name = name;
        this.img = img;
        this.muserURL = muserURL;
        this.isNew = isNew;
    }

    public Muser(Bundle bundle){
        this.nickname = bundle.getString(Constants.muserNickname);
        this.name = bundle.getString(Constants.muserName);
        this.img = bundle.getString(Constants.imgUser);
        this.muserURL =  bundle.containsKey(Constants.muserURL) ? bundle.getString(Constants.muserURL): new String();
        this.isNew = bundle.containsKey(Constants.isNewUrlMuser)? bundle.getBoolean(Constants.isNewUrlMuser) : false;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMuserURL() {
        return muserURL;
    }

    public void setMuserURL(String muserURL) {
        this.muserURL = muserURL;
    }

    public ArrayList getPlayList() {
        return playList;
    }

    public void setPlayList(ArrayList playList) {
        this.playList = playList;
    }
}
