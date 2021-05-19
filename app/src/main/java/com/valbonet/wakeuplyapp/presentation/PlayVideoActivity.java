package com.valbonet.wakeuplyapp.presentation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.valbonet.wakeuplyapp.data.LoadImageTask;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.presentation.alarmclock.ActivityAlarmClock;
import com.valbonet.wakeuplyapp.presentation.alarmclock.AlarmSettings;
import com.valbonet.wakeuplyapp.presentation.alarmclock.AlarmTime;
import com.valbonet.wakeuplyapp.presentation.alarmclock.DbAccessor;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.utils.UtilsVideo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.Calendar;

public class PlayVideoActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener{

    private WebView webView;

    private VideoView videoView;
    private String imgUser;
    private String videoURLPage;
    private String videoURL;
    private String usrNicknameValue;
    private String videoName;
    private String muserURL;
    private boolean isNewURL;

    Long alarmId;

    private ProgressBar progressBar;
    private Button addVideoAlarm;

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.playvideo);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Enable custom action bar
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //We use our custom back button
        getSupportActionBar().hide();

        if (!Utils.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(),
                    R.string.connection_needed,
                    Toast.LENGTH_LONG)
                    .show();
            finish() ;
        }

        activity = this;

        // An alarm id is required in the extras bundle.
        Bundle bundle = getIntent().getExtras();
        imgUser = bundle.getString("imgUser");
        videoURLPage = bundle.getString("videoURLPage");
        videoURL = bundle.getString("videoURL");
        videoName = bundle.getString("videoName");
        muserURL = bundle.containsKey("urlMuser") ? getIntent().getExtras().getString("urlMuser"): new String();
        isNewURL = bundle.containsKey("isNewUrlMuser")? bundle.getBoolean("isNewUrlMuser") : false;
        usrNicknameValue = bundle.getString("nickname");

        progressBar = findViewById(R.id.progressBar_cyclic);

        videoView = findViewById(R.id.myVideo);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);


        if (videoURL == null || (videoURL != null && videoURL.isEmpty())){
            webView = findViewById(R.id.webViewer);
            setWebviewSettings();

            webView.loadUrl(videoURLPage);
            //webView.loadUrl("http://vm.tiktok.com/JSBsCm");
        }else{
            if (!videoView.isPlaying()){
//                String videoNoWatermarkURL = Data.getUrlVideoNoWatermark(videoURL);
//                if (videoNoWatermarkURL != null){
//                    Uri vid = Uri.parse(videoNoWatermarkURL);
//                    UtilsVideo.setVideoURI(videoView, vid);
//                }else {
                    Uri vid = Uri.parse(videoURL);
                    videoView.setVideoURI(vid);
//                }

                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {

                        UtilsVideo.applyScale(activity, mp, videoView);

                        videoView.start();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        Uri vid = Uri.parse(videoURL);
                        videoView.setVideoURI(vid);
                        return true;
                    }
                });
            }

        }

        addVideoAlarm = findViewById(R.id.add_videoAlarm);
        alarmId = (bundle.containsKey("alarmID"))? bundle.getLong("alarmID") : null;
        if (!bundle.containsKey("alarmID")){
            // CODE CREATE NEW ALARM with this tiktok video
            //PUT BUTTON Create Alarm text
            addVideoAlarm.setText(R.string.create_alarm);
        }

        addVideoAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DbAccessor db = new DbAccessor(getApplicationContext());
                Calendar now = Calendar.getInstance();

                if (alarmId == null) {
                    AlarmTime time = new AlarmTime(now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE), 0);
                    alarmId = db.newAlarm(time, false, "");
                }

                AlarmSettings settings = db.readAlarmSettings(alarmId);

                settings.setNickMuser(usrNicknameValue);
                settings.setUrlMuser(Utils.reviewURL(muserURL));
                settings.setVideoAlarmName(videoName);
                settings.setVideoAlarmUrl(Utils.reviewURL(videoURLPage));
                settings.setUrlImageMuser(Utils.reviewURL(imgUser));

                db.writeAlarmSettings(alarmId, settings);
                db.closeConnections();

                Intent myIntent = new Intent(getApplicationContext(), ActivityAlarmClock.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(myIntent);

                finish();
            }
        });

        //User Image
        ImageView userImageView = findViewById(R.id.userImage);
        if (imgUser != null && !imgUser.isEmpty()){
            LoadImageTask loadImage = new LoadImageTask(userImageView, usrNicknameValue);
            loadImage.execute(Utils.reviewURL(imgUser));
        }

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

    }

    private void setWebviewSettings() {
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(this.getWebViewClient());
    }

    @Override
    protected void onDestroy() {
        videoView.pause();
        videoView.stopPlayback();
        videoView.destroyDrawingCache();
        if(webView != null)
            webView.clearCache(true);
        super.onDestroy();
    }

    public WebViewClient getWebViewClient() {

        return new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //"(function(){return window.document.body.outerHTML})();"
                //"setTimeout(function(){ alert(\"Hello\"); }, 3000);"

                view.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                Log.d("HTML", html);

                                String newHTML = html.replaceAll("u003C", "<");
                                //newHTML = StringEscapeUtils.unescapeJava(newHTML);
                                newHTML = newHTML.replaceAll("\\\\", new String());

                                // code here
                                Document doc = Jsoup.parse(newHTML);
                                URL url = null;
                                URL imgUrl = null;
                                try{

                                    //User Nickname
                                    Elements usrNickname = doc.select("span.handler");
                                    if (!usrNickname.isEmpty()){
                                        usrNicknameValue = usrNickname.get(0).html();
                                    }

                                    Elements usrName = doc.select("h1.nickName");
                                    if (!usrName.isEmpty()){
                                        //User Name
                                        if (isNewURL){
                                            String usrNameValue = usrName.get(0).html();
                                            //TODO: code to update the name in database
                                            Data.updateTiktokMuserName(usrNicknameValue, usrNameValue);
                                        }
                                    }

                                    Elements eMuserURL = doc.select("div.text");
                                    if (!eMuserURL.isEmpty()){
                                        Element link = eMuserURL.select("a").first();
                                        muserURL = link.attr("abs:href");
                                    }

                                    //Music Video name
                                    Elements videoMusicName = doc.select("div.music-name");
                                    if (!videoMusicName.isEmpty()){
                                        videoName = videoMusicName.get(0).html();
                                    }

                                    Elements videos = doc.select("div.player");
                                    //Selected video
                                    if (!videos.isEmpty()){
                                        String videoAddress = "https:"+videos.get(0).attr("data-src");

                                        try{

                                            //videoURL = videoAddress;
                                            Uri vid = Uri.parse(videoAddress);
                                            videoView.setVideoURI(vid);

                                            videoView.requestFocus();
                                            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                public void onPrepared(MediaPlayer mp) {
                                                    videoView.start();
                                                }
                                            });

//                                            videoView.setOnErrorListener(vidVwErrorListener);

                                        }catch(Exception e){
                                            Log.e("Error", e.getMessage());
                                            e.printStackTrace();

                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//                try {
//                    // do whatever you want to do on a web link click
//                    view.loadUrl(url);
//                    return true;
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                return super.shouldOverrideUrlLoading(view, url);
//            }


        };
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
       Log.e("Video error", "Revisar error");
        return false;
    }

}
