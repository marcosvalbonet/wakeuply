package com.valbonet.wakeuplyapp.presentation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.model.Video;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.utils.UtilsVideo;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class WebViewVideo extends WebViewClient {

    private Activity activity;
    private VideoView videoView;
    private WebView webView;

    private ProgressBar progressBar;
    private Context context;

    private int volume;

    private Timer timer;
    private boolean finishedRendering = false;


    public WebViewVideo(Activity activity, WebView webView, VideoView videoView){

        this.activity = activity;
        progressBar = activity.findViewById(R.id.progressBar_cyclic);
        context = activity.getApplicationContext();
        this.videoView = videoView;
        this.webView = webView;

        this.volume = 30;
        timer = new Timer();

        this.webView.getSettings().setJavaScriptEnabled(true);

        this.webView.getSettings().setDefaultTextEncodingName("utf-8");
        this.webView.setInitialScale(1);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setSupportZoom(true);
        this.webView.getSettings().setBuiltInZoomControls(false);
        this.webView.requestFocus(View.FOCUS_DOWN);

        this.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.getSettings().setUserAgentString(Constants.userAgent);

        this.webView.setWebChromeClient(webChromeClient);
        this.webView.setWebViewClient(this);

    }

    public void loadURL(String videoPageURL){
        Video video = Data.getVideo(videoPageURL);
        launchVideo(video);

    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        //TOOO: start timeout
        timer.schedule(new URLLoaderTimeoutCheckTask(), 30000);

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

//        final String webURL = url;
//
//        if (url.contains("/video/")){
//
//            String js = "(function(){ var videoURL; var cardVideos = document.getElementsByClassName(\"_global_container\"); var tagVideos = document.getElementsByTagName(\"video\"); if(cardVideos.length >0){videoURL = cardVideos[0].outerHTML;} else {videoURL = tagVideos[0].outerHTML;} return videoURL;})();";
//            view.evaluateJavascript(js,
//                new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String html) {
//                        Log.d("HTML", html);
//
//                        String videoCard = html;
//
//                        String newHTML = videoCard.replaceAll("u003C", "<");
//                        //newHTML = StringEscapeUtils.unescapeJava(newHTML);
//                        videoCard = newHTML.replaceAll("\\\\", new String());
//
//                        // code here
//                        Document doc = Jsoup.parse(videoCard);
//                        Elements videos = doc.select("video._video_card_");
//                        if (videos.isEmpty()){
//                            videos = doc.getElementsByTag("video");
//                        }
//
//                        if (!videos.isEmpty()){
//                            String videoAddress = videos.get(0).attr("src");
//                            launchVideo(videoAddress);
//                        }
//                    }
//                });
//        }else{
//
//            view.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
//                new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String html) {
//                        Log.d("HTML", html);
//
//                        String newHTML = html.replaceAll("u003C", "<");
//                        //newHTML = StringEscapeUtils.unescapeJava(newHTML);
//                        newHTML = newHTML.replaceAll("\\\\", new String());
//
//                        // code here
//                        Document doc = Jsoup.parse(newHTML);
//                        URL url = null;
//                        URL imgUrl = null;
//                        try{
//
//                            Elements videos = doc.select("video._video_card_");
//                            //Selected video
//                            if (!videos.isEmpty()){
//                                String videoAddress = videos.get(0).attr("src");
//                                launchVideo(videoAddress);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//        }
    }

    private void launchVideoFile(File videoFile){

        try{
            UtilsVideo.setVideoPath(videoView, videoFile);
            finishedRendering = true;
            progressBar.setVisibility(View.GONE);

            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    enableSound(volume, mp);
                    videoView.start();
                }
            });

        }catch(Exception e){
            Log.e("Error", e.getMessage());
            e.printStackTrace();

        }
    }

    private void launchVideo(Video video){

        try{
            Uri vid = Uri.parse(Utils.reviewVideoURL(video.getVideoURL()));
            UtilsVideo.setVideoURI(videoView, vid, video.getCookie());
            finishedRendering = true;
            progressBar.setVisibility(View.GONE);

            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    enableSound(volume, mp);
                    videoView.start();
                }
            });

        }catch(Exception e){
            Log.e("Error", e.getMessage());
            e.printStackTrace();

        }
    }


    public void setVolume(int volume){
        this.volume = volume;
    }

    public void enableSound(int sound, MediaPlayer mp){
        Float f = Float.valueOf(sound);
        //Float ft = Utils.getFloatVolume(sound);
        Log.e("checkingsounds","&&&&&   "+f);
        mp.setVolume(f,f);
        mp.setLooping(true);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //Max Volume 15
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  //this will return current volume.

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Utils.get15Volume(sound), AudioManager.FLAG_PLAY_SOUND);   //here you can set custom volume.
    }

    WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);

            if ( newProgress >=80 ) {
                progressBar.setVisibility(View.GONE);
            }

        }

    };

    private long getTiktokVideo(){
        int nbfile = (int)(Math.random() * 7) + 1;
        long video;

        switch (nbfile){
            case 1:
                video = R.raw.tiktokvideo1;
                break;
            case 2:
                video = R.raw.tiktokvideo2;
                break;
            case 3:
                video = R.raw.tiktokvideo3;
                break;
            case 4:
                video = R.raw.tiktokvideo4;
                break;
            case 5:
                video = R.raw.tiktokvideo5;
                break;
            case 6:
                video = R.raw.tiktokvideo6;
                break;
            case 7:
                video = R.raw.tiktokvideo7;
                break;
            default:
                video = R.raw.tiktokvideo1;
        }

        return video;
    }

    class URLLoaderTimeoutCheckTask extends TimerTask {

        public void run() {
            if(!finishedRendering){
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        webView.stopLoading();

                        long file = getTiktokVideo();
                        String uri = "android.resource://" + context.getPackageName() + "/raw/" + file;
                        Uri path = Uri.parse(uri);
                        videoView.setVideoURI(path);
                        videoView.start();
                    }
                });
            }
        }
    }



}
