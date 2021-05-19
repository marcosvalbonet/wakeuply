package com.valbonet.wakeuplyapp.presentation.searcher;

import java.util.UUID;

/**
 * Entidad Lead
 */
public class Lead {
    //TODO MVAL: Is necessary mId?
    private String mId;
    private String mName;
    private String nickname;
    private String userId;
    private String linkUrl;
    private int mImage;
    private String imgSrc;

    public Lead(String nickname, String name, String userId, String url, String imgSrc) {
        mId = UUID.randomUUID().toString();
        mName = name;
        this.userId = userId;
        this.nickname = nickname;
        linkUrl = url;
        this.imgSrc = imgSrc;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nick) {
        this.nickname = nick;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String url) {
        this.linkUrl = url;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }

    public String getURLImage() {
        return imgSrc;
    }

    public void setURLImage(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    @Override
    public String toString() {
        return "Lead{" +
                "ID='" + mId + '\'' +
                ", Compañía='" + linkUrl + '\'' +
                ", Nombre='" + mName + '\'' +
                ", Cargo='" + nickname + '\'' +
                '}';
    }
}
