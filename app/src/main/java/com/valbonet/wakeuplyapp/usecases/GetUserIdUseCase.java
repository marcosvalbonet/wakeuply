package com.valbonet.wakeuplyapp.usecases;


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

public class GetUserIdUseCase extends AsyncTask<String, Void, String> {

    HttpGet httpget;
    DefaultHttpClient httpclient;
    private String URL;
    private HttpResponse response;

    @Override
    protected String doInBackground(String... params) {
        String tacResult = null;

        httpclient = new DefaultHttpClient();
        httpget = new HttpGet();
        URL = "https://www.tiktok.com/"+ params[0];

        try {
            URI website = new URI(URL);
            httpget.setURI(website);
            response = httpclient.execute(httpget);

            java.net.URL urlToConnect = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToConnect.openConnection();
            InputStream inS = new BufferedInputStream(urlConnection.getInputStream());
            String resHTML = readStream(inS);
            if (resHTML == null) return null;

//            String substring = resHTML.substring(resHTML.indexOf("\"userInfo\":{\"user\":{\"id\":")+25);
//            String userId = substring.substring(1, substring.indexOf("\",\""));

            //String regex = "<script id=\"__NEXT_DATA__\" type=\"application/json\" crossorigin=\"anonymous\">(.*)</script><script crossorigin=\"anonymous\" nomodule=";


            tacResult = findUserId(resHTML) + ";" + findSecUid(resHTML);

//            tacResult = userId;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return tacResult;
    }

    public String findSecUid(String resHTML){
        String tacResult = null;
        String regex = "\"secUid\":\"(.*)\",\"";

        Pattern patron = Pattern.compile(regex);
        Matcher matcher = patron.matcher(resHTML);

        if (matcher.find()) {
            if (matcher.group(1).isEmpty()){
                tacResult = findSecUid(resHTML.substring(resHTML.indexOf(regex), resHTML.length()));
            }else{
                tacResult = matcher.group(1);
            }
            tacResult = tacResult.substring(0, tacResult.indexOf(",")-1);
        }
        return tacResult;
    }

    public String findUserId(String resHTML){
        String tacResult = null;
        String regex = "\"user\":\\{\"id\":\"(.*)\",\"";

        Pattern patron = Pattern.compile(regex);
        Matcher matcher = patron.matcher(resHTML);

        if (matcher.find()) {
            if (matcher.group(1).isEmpty()){
                tacResult = findUserId(resHTML.substring(resHTML.indexOf(regex), resHTML.length()));
            }else{
                tacResult = matcher.group(1);
            }
            tacResult = tacResult.substring(0, tacResult.indexOf(",")-1);
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
