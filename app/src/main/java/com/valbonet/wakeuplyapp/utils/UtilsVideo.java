package com.valbonet.wakeuplyapp.utils;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.valbonet.wakeuplyapp.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UtilsVideo {

    /**  getScale(mp, videoView)
     *  576 videoWidth
     * 1024 videoHeight
     *
     * 1080 screen Width
     * 2221 screen Height
     *
     * 1080 / 576 = 1.875
     * 1024 * 1.875 = 1920
     *
     * 2221 / 1920 = 1.15f
     */
    public static void applyScale(Activity activity, MediaPlayer mp, VideoView videoView) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int videoWidth = mp.getVideoWidth();
        int videoHeight= mp.getVideoHeight();

        if (videoWidth < videoHeight) {

            float widthFactor = (float)metrics.widthPixels / (float)videoWidth;
            float videoHeightConverter = videoHeight * widthFactor;

            if (videoHeightConverter < metrics.heightPixels){
                float heightFactor = (float) metrics.heightPixels / videoHeightConverter;

                ViewGroup.LayoutParams params = videoView.getLayoutParams();
                if (params instanceof FrameLayout.LayoutParams){
                    FrameLayout.LayoutParams castParams = (FrameLayout.LayoutParams)params;
                    castParams.gravity = Gravity.TOP;
                    castParams.width =  metrics.widthPixels;
                    castParams.height = metrics.heightPixels;
                    castParams.leftMargin = 0;
                    videoView.setLayoutParams(castParams);
                }else if (params instanceof RelativeLayout.LayoutParams){
                    RelativeLayout.LayoutParams castParams = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
                    castParams.addRule(RelativeLayout.ALIGN_TOP);
                    //castParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                    castParams.width =  metrics.widthPixels;
                    castParams.height = metrics.heightPixels;
                    videoView.setLayoutParams(castParams);
                }

                videoView.setScaleX(heightFactor);
                videoView.setScaleY(heightFactor);
            }
        }
    }

    /**
     * Change User Agent in VideoView
     * @param vp
     * @param uri
     */
    public static void setVideoURI(VideoView vp, Uri uri){
        String referer="referer: https://www.tiktok.com/\r\n",
                useragent="USER-AGENT: okhttp\r\n";
        try{
            Map<String,String> mhead=new HashMap<String,String>();
            //if 5.X or higher, Fix User-Agent be force set by system
//            mhead.put(referer.split(": ")[0],referer.split(": ")[1]);
//            mhead.put(useragent.split(": ")[0],useragent.split(": ")[1]);//"User-Agent" => "USER-AGENT"

            mhead.put("cookie","tt_webid_v2=6812345678914");//"Cookie" => "Cookie"
            mhead.put("user-agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");//"User-Agent" => "USER-AGENT"
            mhead.put("referer","https://www.tiktok.com/");
            mhead.put("authority", "m.tiktok.com");
            mhead.put("method", "GET");
            mhead.put("scheme", "https");
            mhead.put("accept", "application/json, text/plain, */*");
            mhead.put("accept-encoding", "gzip, deflate, br");
            mhead.put("accept-language", "en-US,en;q=0.9");
            mhead.put("sec-fetch-dest", "empty");
            mhead.put("sec-fetch-mode", "cors");
            mhead.put("sec-fetch-site", "same-site");

            Class vpclass=Class.forName("android.widget.VideoView");
            Method[]ms=vpclass.getMethods();
            Method mTarget=null;
            for(Method m:ms){
                if(m.getName().equals("setVideoURI"))
                    if(m.getParameterTypes().length==2) mTarget=m;
            }
            if(mTarget==null){
                //AlertDialog(this,"Set Headers Fail","Sorry... your device is not support this way.","ok");
            }else{
                mTarget.invoke(vp,new Object[]{uri,mhead});
            }
        }catch(Exception e){
            //AlertDialog(this,"Set Headers Fail",Arrays.toString(e.getStackTrace()),"ok");
            e.printStackTrace();
        }

    }

    public static void setVideoURI(VideoView vp, Uri uri, String cookie){
        try{
            Map<String,String>  mhead=new HashMap<String,String>();
            //if 5.X or higher, Fix User-Agent be force set by system
            //mhead.put("cookie","tt_webid_v2=6812345678918");//"Cookie" => "Cookie"
            mhead.put("cookie", cookie);//"Cookie" => "Cookie"
//            mhead.put("user-agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");//"User-Agent" => "USER-AGENT"
            mhead.put("user-agent", Constants.userAgent);//"User-Agent" => "USER-AGENT"
            mhead.put("referer","https://www.tiktok.com/");
            mhead.put("authority", "m.tiktok.com");
            mhead.put("authority", "www.tiktok.com");
            mhead.put("method", "GET");
            mhead.put("scheme", "https");
            mhead.put("accept", "application/json, text/plain, */*");
//            mhead.put("accept-encoding", "gzip, deflate, br");
//            mhead.put("accept-language", "en-US,en;q=0.9");
//            mhead.put("sec-fetch-dest", "empty");
//            mhead.put("sec-fetch-mode", "cors");
//            mhead.put("sec-fetch-site", "same-site");
//            mhead.put("cache-control", "no-cache");
            mhead.put("dnt", "1");
//            mhead.put("origin", "https://m.tiktok.com/");
//            mhead.put("pragma", "no-cache");

            Class vpclass=Class.forName("android.widget.VideoView");
            Method[]ms=vpclass.getMethods();
            Method mTarget=null;
            for(Method m:ms){
                if(m.getName().equals("setVideoURI"))
                    if(m.getParameterTypes().length==2) mTarget=m;
            }
            if(mTarget==null){
                //AlertDialog(this,"Set Headers Fail","Sorry... your device is not support this way.","ok");
            }else{
                mTarget.invoke(vp,new Object[]{uri,mhead});
            }
        }catch(Exception e){
            //AlertDialog(this,"Set Headers Fail",Arrays.toString(e.getStackTrace()),"ok");
            e.printStackTrace();
        }


    }

    /**
     * Change User Agent in VideoView
     * @param vp
     * @param file
     */
    public static void setVideoPath(VideoView vp, File file){
        try{
            vp.setVideoPath(file.getPath());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
        out.close();
    }

    public static String createCookieVideo(){
        Integer cookieRandom = (int)(Math.random()*1000000000);
        //tt_webid_v2=6812345678918
        return "tt_webid_v2=6812" + cookieRandom;
    }

}
