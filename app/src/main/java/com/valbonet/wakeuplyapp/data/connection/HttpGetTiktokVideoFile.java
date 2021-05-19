package com.valbonet.wakeuplyapp.data.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.valbonet.wakeuplyapp.utils.UtilsVideo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Get the Url TikTok video from Tiktok video page
 */
public class HttpGetTiktokVideoFile extends AsyncTask<String, Void, String> {

    HttpGet httpget;
    DefaultHttpClient httpclient;
    private String URL;
    private File videoFile;
    private HttpResponse response;


    public HttpGetTiktokVideoFile(String videoPageURL, File videoFile){

        URL = videoPageURL;
        this.videoFile = videoFile;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlResult = null;

        httpclient = new DefaultHttpClient();
        httpget = new HttpGet();

        try {
//            URI website = new URI(URL);
//            httpget.setURI(website);
//            response = httpclient.execute(httpget);

            java.net.URL urlToConnect = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToConnect.openConnection();
            String myCookie = "tt_webid_v2=6812345678914" ;
            urlConnection.setRequestProperty("Cookie", myCookie);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            urlConnection.setRequestProperty("REFERER", "https://www.tiktok.com/");

            urlConnection.setRequestProperty("authority", "m.tiktok.com");
            urlConnection.setRequestProperty("method", "GET");
            urlConnection.setRequestProperty("scheme", "https");
            urlConnection.setRequestProperty("accept", "application/json, text/plain, */*");
            urlConnection.setRequestProperty("accept-encoding", "gzip, deflate, br");
            urlConnection.setRequestProperty("accept-language", "en-US,en;q=0.9");
            urlConnection.setRequestProperty("sec-fetch-dest", "empty");
            urlConnection.setRequestProperty("sec-fetch-mode", "cors");
            urlConnection.setRequestProperty("sec-fetch-site", "same-site");

            InputStream inS = new BufferedInputStream(urlConnection.getInputStream());
            //String resHTML = readStream(inS);
            //if (resHTML == null) return null;

            /*Cretae File with inputStream to outputStream
            */
            FileOutputStream out = new FileOutputStream(videoFile);
            UtilsVideo.copyStream(inS, out);

        } catch (IOException e) {
            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
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

