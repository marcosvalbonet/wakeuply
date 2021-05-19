package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MusicControl {
    private static MusicControl sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    public MusicControl(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
    }

    public static MusicControl getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MusicControl(context);
        }
        return sInstance;
    }

    public void playMusic(Context context, Uri song) {
        //mMediaPlayer = MediaPlayer.create(context, song);
        try {
            mMediaPlayer.setDataSource(context, song);
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            mMediaPlayer.stop();
        }
    }

    public void stopMusic() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.seekTo(0);
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
