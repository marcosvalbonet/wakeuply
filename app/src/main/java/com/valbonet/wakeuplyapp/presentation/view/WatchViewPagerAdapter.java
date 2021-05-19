package com.valbonet.wakeuplyapp.presentation.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.valbonet.wakeuplyapp.data.LoadImageTask;
import com.valbonet.wakeuplyapp.presentation.PlayUserWebActivity;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.presentation.alarmclock.ActivityAlarmClock;
import com.valbonet.wakeuplyapp.presentation.alarmclock.AlarmSettings;
import com.valbonet.wakeuplyapp.presentation.alarmclock.AlarmTime;
import com.valbonet.wakeuplyapp.presentation.alarmclock.DbAccessor;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.model.Muser;
import com.valbonet.wakeuplyapp.model.Tiktok;
import com.valbonet.wakeuplyapp.model.Video;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.utils.UtilsVideo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class WatchViewPagerAdapter extends RecyclerView.Adapter<WatchViewPagerAdapter.ViewHolder> {

    private List<Tiktok> mData;
    private LayoutInflater mInflater;
    private Muser muser;
    private Activity activity;
    private Long alarmId;

    ViewPager2 viewPager2;

    private List<ViewHolder> viewHolder;


    public WatchViewPagerAdapter(Context context, ViewPager2 viewPager2, List<Tiktok> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        if (context instanceof Activity){
            this.activity = (Activity) context;
        }
        this.viewPager2 = viewPager2;
        viewHolder = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_viewpager, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tiktok tiktok = mData.get(position);
        if (tiktok == null || (tiktok!=null && tiktok.getVideo() == null)){
            findNextRandomTikTok(holder, position);
        }else{
            loadingTiktok(holder, tiktok, position);
//            Tiktok nextTiktok = mData.get(position +1);
//            if (nextTiktok == null || (nextTiktok!=null && nextTiktok.getVideo() == null)){
//                findNextRandomTikTok(holder, nextTiktok);
//            }
        }
    }

   public void play(int position){
       if (position < viewHolder.size()){
           ViewHolder holder = viewHolder.get(position);
           if (holder != null){
               pause(position);
               holder.videoView.start();
               Log.e("Status", "play"+ position + " " +holder.titleView.getText());
           }
       }
   }

    public void pause(int position){
        for (int i = 0; i < viewHolder.size(); i++){
            if (i != position){
                ViewHolder holder = viewHolder.get(i);
                if (holder != null){
                    holder.videoView.pause();
                    Log.e("Status", "pause:"+ i);
                }
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.addVideoAlarm.setVisibility(View.VISIBLE);

    }

    public void setViewPager(ViewPager2 viewPager2){
        this.viewPager2 = viewPager2;
    }

    public void setList(List<Tiktok> list) {
        this.mData = list;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        String videoUrlNoWatermark;
        VideoView videoView;
        AppCompatImageView addVideoAlarm;
        ImageView userImageView;
        TextView userNameView;
        TextView titleView;
        TextView musicView;
        ImageView tiktokShare;

        ViewHolder(final View itemView) {
            super(itemView);

            //myVideo
            videoView = itemView.findViewById(R.id.myVideo);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {

                    UtilsVideo.applyScale(activity, mp, videoView);
                    //videoView.start();
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener(){
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Video nowVideo = mData.get(i).getVideo();
                    if (nowVideo != null){
                        Uri vid = Uri.parse(nowVideo.getVideoURL());
                        //videoView.setVideoURI(vid);
                        UtilsVideo.setVideoURI(videoView, vid, nowVideo.getCookie());
                        //TODO MVAL: save the position and when this position is called
                        // we need to see if this position is played or is the next one
                        // that we must to play
                        //savePosition?
                    }
                    Log.e("Video error", "Revisar error");
                    return true;

                }
            });

            //Muser Image
            userImageView = itemView.findViewById(R.id.image_view_profile_pic);
            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO MVAL Call to PlayUserActivity
                    int position = viewPager2.getCurrentItem();
                    Muser muser = mData.get(position).getMuser();

                    Intent myIntent = new Intent(activity, PlayUserWebActivity.class);
                    myIntent.putExtra("alarmID", alarmId);
                    myIntent.putExtra("urlUser", muser.getMuserURL());
                    myIntent.putExtra("nickUser", muser.getNickname());
                    myIntent.putExtra("nameUser", muser.getName());
                    myIntent.putExtra("userImgURL", muser.getImg());
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.getApplicationContext().startActivity(myIntent);
                }

            });
//            if (muser!= null && muser.getImg() != null && !muser.getImg().isEmpty()){
//                LoadImageTask loadImage = new LoadImageTask(userImageView, muser.getNickname());
//                loadImage.execute(Utils.reviewURL(muser.getImg()));
//            }

            //Muser name
            userNameView = itemView.findViewById(R.id.text_view_account_handle);
//            userNameView.setText(muser.getNickname());

            //Tiktok title
            titleView = itemView.findViewById(R.id.text_view_video_description);

            //Tiktok music
            musicView = itemView.findViewById(R.id.text_view_music_title);

            //Tiktok share
            tiktokShare = itemView.findViewById(R.id.share);
            tiktokShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewPager2.getCurrentItem();
                    Video thisVideo = mData.get(position).getVideo();
                    //https://wakeuply.page.link/?link=&apn=com.valbonet.wakeuplyapp
                    String dynamicLink = "https://wakeuply.page.link/?link="+
                            thisVideo.getVideoURLPage()+
                            "&apn=com.valbonet.wakeuplyapp";

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Compartir");
                    i.putExtra(Intent.EXTRA_TEXT, dynamicLink);
                    activity.startActivity(Intent.createChooser(i, "Compartir Tiktok"));
                }
            });

            addVideoAlarm = itemView.findViewById(R.id.alarm_view);
            Bundle bundle = activity.getIntent().getExtras();
            alarmId = (bundle != null && bundle.containsKey("alarmID"))? bundle.getLong("alarmID") : null;

            if (alarmId == null){
                // CODE CREATE NEW ALARM with this tiktok video
                //PUT BUTTON Create Alarm text
                //addVideoAlarm.setText(R.string.create_alarm);
                //TODO: change Icon to V when the video is changed
            }
            addVideoAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbAccessor db = new DbAccessor(activity);
                    Calendar now = Calendar.getInstance();

                    if (alarmId == null) {
                        AlarmTime time = new AlarmTime(now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE), 0);
                        alarmId = db.newAlarm(time, false, "");
                    }

                    AlarmSettings settings = db.readAlarmSettings(alarmId);

                    int position = viewPager2.getCurrentItem();
                    Video thisVideo = mData.get(position).getVideo();

                    settings.setNickMuser(muser.getNickname());
                    settings.setUrlMuser(Utils.reviewURL(muser.getMuserURL()));
                    settings.setVideoAlarmName(thisVideo.getVideoName());
                    settings.setVideoAlarmUrl(Utils.reviewURL(thisVideo.getVideoURLPage()));
                    settings.setUrlImageMuser(Utils.reviewURL(muser.getImg()));

                    db.writeAlarmSettings(alarmId, settings);
                    db.closeConnections();

                    Intent myIntent = new Intent(activity, ActivityAlarmClock.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(myIntent);

                    activity.finish();
                }
            });

        }
    }

    private void findNextRandomTikTok(final ViewHolder holder, final int position){


//        /*Comment to only get one video*/
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

//                List<Tiktok> list = Data.getRandomTiktokMusers(1);
//                for(Tiktok t : list){
//                }
                //TODO MVAL: we need to change thread to AndroidX library
                // ReactiveX
                Data.getRandomTiktoks(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Object o) {
                        if (o instanceof ArrayList) {
                            ArrayList<Tiktok> list = ((ArrayList<Tiktok>) o);
                            for (Tiktok t : list) {
                                Tiktok tiktok = mData.get(position);
                                tiktok.setMuser(t.getMuser());
                                tiktok.setVideo(t.getVideo());
                                loadingTiktok(holder, tiktok, position);
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
            }
        });
            thread.start();

    }

    public void loadingTiktok(final ViewHolder holder, final Tiktok tiktok, int position){
        muser = tiktok.getMuser();

        if (muser!= null && muser.getImg() != null && !muser.getImg().isEmpty()){
            LoadImageTask loadImage = new LoadImageTask(holder.userImageView, muser.getNickname());
            loadImage.execute(Utils.reviewURL(muser.getImg()));

            holder.userNameView.setText(muser.getNickname());
        }

        Video nowVideo = tiktok.getVideo();
        if (nowVideo != null){
            String videoNoWatermarkURL = nowVideo.getVideoURLNoWatermark();
            //            if(videoNoWatermarkURL == null)
            //                videoNoWatermarkURL = Data.getUrlVideoNoWatermark(nowVideo.getVideoURL());

            if(videoNoWatermarkURL != null && !videoNoWatermarkURL.isEmpty()){
                holder.videoUrlNoWatermark =  videoNoWatermarkURL;
                Uri vid = Uri.parse(holder.videoUrlNoWatermark);
                UtilsVideo.setVideoURI(holder.videoView, vid, nowVideo.getCookie());
                holder.videoView.start();

            }else{
                Uri vid = Uri.parse(nowVideo.getVideoURL());
                UtilsVideo.setVideoURI(holder.videoView, vid, nowVideo.getCookie());
                holder.titleView.setText(nowVideo.getVideoName());
                holder.musicView.setText(nowVideo.getMusicName());
            }

            viewHolder.add(position, holder);
        }

    }
}
