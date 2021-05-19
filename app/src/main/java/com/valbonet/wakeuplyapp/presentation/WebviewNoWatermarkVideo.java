package com.valbonet.wakeuplyapp.presentation;

import android.net.Uri;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

public class WebviewNoWatermarkVideo extends WebViewClient {


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        String js = "(function(){ var tagVideos = document.getElementsByTagName(\"video\"); if(tagVideos.length >0){ tagVideos[0].loop=true; tagVideos[0].controls = false; tagVideos[0].play(); return tagVideos[0].getElementsByTagName(\"source\")[0].src;} return \"hola\";})();";
        view.evaluateJavascript(js,
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        Log.d("Tag Video", html);
                        String urlNoWatermarkvideo = html.substring(html.indexOf("http"), html.lastIndexOf("\""));
//                        Uri vid = Uri.parse(urlNoWatermarkvideo);
//                        videoView.setVideoURI(vid);
//                        if (startVideo) {
//                            videoView.start();
//                        }else{
//                            videoView.pause();
//                        }
                    }
                });
    }

}
