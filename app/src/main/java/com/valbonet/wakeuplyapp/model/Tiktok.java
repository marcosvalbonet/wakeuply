package com.valbonet.wakeuplyapp.model;

public class Tiktok {
    Muser muser;
    Video video;

    public Tiktok(Muser muser, Video video){
        this.muser = muser;
        this.video = video;
    }

    public Muser getMuser() {
        return muser;
    }

    public void setMuser(Muser muser) {
        this.muser = muser;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
