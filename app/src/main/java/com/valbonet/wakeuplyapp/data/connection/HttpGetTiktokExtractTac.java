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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Get the Url TikTok video from Tiktok video page
 */
public class HttpGetTiktokExtractTac extends AsyncTask<String, Void, String> {

    HttpGet httpget;
    DefaultHttpClient httpclient;
    private String URL;
    private HttpResponse response;

    @Override
    protected String doInBackground(String... params) {
        String tacResult = null;

        httpclient = new DefaultHttpClient();
        httpget = new HttpGet();
        URL = "https://www.tiktok.com/discover";

        try {
            URI website = new URI(URL);
            httpget.setURI(website);
            response = httpclient.execute(httpget);

            java.net.URL urlToConnect = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToConnect.openConnection();
            InputStream inS = new BufferedInputStream(urlConnection.getInputStream());
            String resHTML = readStream(inS);
            if (resHTML == null) return null;

            //String regex = "<script id=\"__NEXT_DATA__\" type=\"application/json\" crossorigin=\"anonymous\">(.*)</script><script crossorigin=\"anonymous\" nomodule=";
            String regex = "<script>tac=(.*)</script>";
            Pattern patron = Pattern.compile(regex);
            Matcher matcher = patron.matcher(resHTML);
            matcher.find();

            tacResult = matcher.group(1);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return tacResult;
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

