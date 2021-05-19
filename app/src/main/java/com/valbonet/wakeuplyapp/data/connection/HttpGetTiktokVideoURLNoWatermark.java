package com.valbonet.wakeuplyapp.data.connection;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Get the Url TikTok video from Tiktok video
 */
public class HttpGetTiktokVideoURLNoWatermark extends AsyncTask<String, Void, String> {

    HttpGet httpget;
    DefaultHttpClient httpclient;
    private String URL;
    private HttpResponse response;

    public HttpGetTiktokVideoURLNoWatermark(String videoURL){
        URL = videoURL;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlResult = "";

        httpclient = new DefaultHttpClient();
        httpget = new HttpGet();

        try {
            java.net.URL urlToConnect = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToConnect.openConnection();
            String myCookie = "6812345678912" ;
            urlConnection.setRequestProperty("Cookie", myCookie);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            urlConnection.setRequestProperty("REFERER", "https://www.tiktok.com");

            InputStream inS = new BufferedInputStream(urlConnection.getInputStream());
            String resHTML = readStream(inS);
            if (resHTML == null) return null;

            int position = resHTML.indexOf("vid:");
            if (position != -1){
                urlResult = resHTML.substring(position+4, position+36);
            }

//            String videoUrl = "https://api.tiktokv.com/aweme/v1/play/?video_id="+urlResult+"&line=0&ratio=720p&watermark=0&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0&logo_name=tiktok";

//            urlToConnect = new URL(videoUrl);
//            urlConnection = (HttpURLConnection) urlToConnect.openConnection();
//            inS = new BufferedInputStream(urlConnection.getInputStream());
//            resHTML = readStream(inS);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlResult;
    }

    public String readStream(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("VIDEOURL", "IOException", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("VIDEOURL", "IOException", e);
            }
        }
        return sb.toString();
    }
}

