package com.valbonet.wakeuplyapp.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.valbonet.wakeuplyapp.ImageAdapter;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PlayHashtagActivity extends AppCompatActivity {

    WebView webView;
    GridView gridview;
    ImageAdapter imageAdapter;
    private ActionBar actionBar;

    private String tag;
    private String urlImg;
    private String usrNicknameValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.playtag);

        webView = findViewById(R.id.webViewer);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.requestFocus(View.FOCUS_DOWN);

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(this.getWebViewClient());

        Bundle bundle = getIntent().getExtras();
        String urlTag = bundle.getString("urlTag");
        tag = bundle.getString("tag");

        actionBar.setTitle(tag);

        //Load URL
        webView.loadUrl(urlTag);

        gridview = findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String dataid = imageAdapter.getVideosURL(position);

                String newUrl = "https://m.tiktok.com/v/"+dataid+".html";
                String imgURL = Utils.reviewURL(imageAdapter.getThumbs(position));

                final Intent myIntent = new Intent(v.getContext(), PlayVideoActivity.class);
                if (imgURL != null) myIntent.putExtra("imgUser", imgURL);
                if (tag != null) myIntent.putExtra("nickname", tag);
                myIntent.putExtra("videoURL", newUrl);
                myIntent.putExtra("alarmID", getIntent().getExtras().getLong("alarmID"));
                v.getContext().startActivity(myIntent);

//                Toast.makeText(getApplicationContext(), "" + position,
//                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

                final String webURL = url;


                view.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                Log.d("HTML", html);
                                if (html.length() < 100) {
                                    return;
                                }

                                String newHTML = html.replaceAll("u003C", "<");
                                //newHTML = StringEscapeUtils.unescapeJava(newHTML);
                                newHTML = newHTML.replaceAll("\\\\", new String());

                                // code here
                                Document doc = Jsoup.parse(newHTML);
                                URL imgUrl = null;
                                String imgSRC = null;

                                try{
                                    Elements usrImg = doc.select("img.head-portrait");


                                    //User Nickname
                                    Elements usrNickname = doc.select("h1");
                                    if (!usrNickname.isEmpty()){
                                        usrNicknameValue = usrNickname.get(0).html();
                                        TextView nickname = findViewById(R.id.nickname);
                                        nickname.setText(usrNicknameValue);
                                    }

                                    //User Videos
                                    ArrayList<String> videoImgSrc = new ArrayList<String>();
                                    ArrayList<String> videoUrl = new ArrayList<String>();
                                    Elements videos = doc.select("div.hot-video-item");

                                    //Relaunch onPageFinished whether the videos aren´t loaded yet
                                    if (videos.isEmpty()){
                                        onPageFinished(webView, webURL);
                                    }

                                    for (Element video : videos){
                                        String videoURL = video.attr("data-id");
                                        videoUrl.add(videoURL);
                                    }

                                    Elements imgs = doc.select("div.cover");
                                    for (Element img : imgs){
                                        imgSRC = img.attr("data-src");
                                        videoImgSrc.add("https:"+imgSRC);
                                    }

                                    imageAdapter.setThumbs(videoImgSrc);
                                    imageAdapter.setVideosURL(videoUrl);

                                    imageAdapter.notifyDataSetChanged();
                                    gridview.setAdapter(imageAdapter);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                String newUrl ;
                try {
                    String mode = StringUtils.substringBetween(url, "musically://", "?");
                    if ("musical".equals(mode)){
                        //TODO: musically://musical?id=6607845384142195973&refer=web&gd_label
                        //String id = "6607845384142195973";
                        //String urlVideo = "https://m.tiktok.com/v/"+id+".html";
                        String id = StringUtils.substringBetween(url, "id=", "&");
                            newUrl = "https://m.tiktok.com/v/"+id+".html";

                    }else if ("profile".equals(mode)){
                        //TODO: musically://profile?id=6607822897996333062&refer=web&gd_label
                        //id="116740719360233472";
                        //String urlUser = "https://m.tiktok.com/h5/share/usr/116740719360233472.html";

                        String id = StringUtils.substringBetween(url, "id=", "&");
                        newUrl = "https://m.tiktok.com/h5/share/usr/"+id+".html";


                    }else{
                        newUrl = url;
                    }

                    // do whatever you want to do on a web link click
                    view.loadUrl(newUrl);
                    return true;


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return super.shouldOverrideUrlLoading(view, url);
            }


        };
    }

    WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//            progressBar.setProgress(newProgress);

        }

    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            webView.setVisibility(View.GONE);
        }
    }

}
