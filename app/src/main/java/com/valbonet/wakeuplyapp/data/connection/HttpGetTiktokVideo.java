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
import java.net.URLDecoder;

/*
 * Get the Url TikTok video from Tiktok video page
 */
public class HttpGetTiktokVideo extends AsyncTask<String, Void, String> {

    HttpGet httpget;
    DefaultHttpClient httpclient;
    private String URL;
    private String cookie;
    private HttpResponse response;

    public HttpGetTiktokVideo(String videoPageURL, String cookie){
        URL = videoPageURL;
        this.cookie = cookie;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlResult = null;
        String urlTitle = null;
        String musicTitle = null;
        String jsonResult = null;

        httpclient = new DefaultHttpClient();
        httpget = new HttpGet();

        try {
            URI website = new URI(URL);
            httpget.setURI(website);
            response = httpclient.execute(httpget);

            java.net.URL urlToConnect = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToConnect.openConnection();
           // String myCookie = "tt_webid_v2=6812345678918" ;
            urlConnection.setRequestProperty("Cookie", cookie);
            // urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            urlConnection.setRequestProperty("REFERER", "https://www.tiktok.com/");

            InputStream inS = new BufferedInputStream(urlConnection.getInputStream());
            String resHTML = readStream(inS);
            if (resHTML == null) return null;

            //Get Json info
            String regexInit = "<script id=\"__NEXT_DATA__\"";
            String endRegexInit = ">";

            String regexFinal = "</script>";

            if(resHTML.indexOf(regexInit)!= -1)   {
                jsonResult = resHTML.substring(resHTML.indexOf(regexInit)+ regexInit.length());
                jsonResult = jsonResult.substring(jsonResult.indexOf(endRegexInit) + endRegexInit.length(), jsonResult.length());
                jsonResult = jsonResult.substring(0, jsonResult.indexOf(regexFinal));

                //TODO: return json and JSONConverter to class Tiktok with Video and muser
            }


                //Get Video
            String regex = "property=\"og:video\" content=\"";
            String regex2 = "playAddr";

            //TODO: Get Title
            String regexTitle = "property=\"og:title\" content=\"";
            String regexTitle2 = "<title>";

            //TODO: Get Description
            String regexDescription = "";

            //TODO: Get Song
            String regexMusic = "\"music\":{";
            String regexSongTitle = "\"title\":\"";

            if(resHTML.indexOf(regex2)!= -1) {
                urlResult = resHTML.substring(resHTML.indexOf(regex2)+regex2.length()+3);
                urlResult = urlResult.substring(0, urlResult.indexOf("\""));

                urlResult = urlResult.replaceAll("\u0026", "&");
                urlResult = urlResult.replaceAll("\\u0026", "&");
                urlResult = urlResult.replaceAll("\\\u0026", "&");
                urlResult = urlResult.replaceAll("\\\\u0026", "&");
                urlResult = URLDecoder.decode(urlResult, "utf-8");

            } else if(resHTML.indexOf(regex)!= -1) {
                    urlResult = resHTML.substring(resHTML.indexOf(regex)+regex.length());
                    urlResult = urlResult.substring(0, urlResult.indexOf("\""));

                    urlResult = urlResult.replaceAll("amp;", "");
                    urlResult = URLDecoder.decode(urlResult, "utf-8");


//            } else if(resHTML.indexOf(regex2)!= -1)   {
//                urlResult = resHTML.substring(resHTML.indexOf(regex2)+regex2.length()+3);
//                urlResult = urlResult.substring(0, urlResult.indexOf("\""));
//
//                urlResult = urlResult.replaceAll("\u0026", "&");
//                urlResult = urlResult.replaceAll("\\u0026", "&");
//                urlResult = urlResult.replaceAll("\\\u0026", "&");
//                urlResult = urlResult.replaceAll("\\\\u0026", "&");
//                urlResult = URLDecoder.decode(urlResult, "utf-8");

            }else{
                Log.e("NoURLVideo", "Video Privado");
            }

            //Title or description
            if(resHTML.indexOf(regexTitle)!= -1) {
                urlTitle = resHTML.substring(resHTML.indexOf(regexTitle)+regexTitle.length());
                urlTitle = urlTitle.substring(0, urlTitle.indexOf("\""));

                urlTitle = urlTitle.replaceAll("amp;", "");
//                urlTitle = URLDecoder.decode(urlTitle, "utf-8");

            } else if(resHTML.indexOf(regexTitle2)!= -1)   {
                urlTitle = resHTML.substring(resHTML.indexOf(regexTitle)+regexTitle.length());
                urlTitle = urlTitle.substring(0, urlTitle.indexOf("<\""));

                urlTitle = urlTitle.replaceAll("amp;", "");
                urlTitle = URLDecoder.decode(urlTitle, "utf-8");
            }

            //Sound
            if(resHTML.indexOf(regexMusic)!= -1) {
                musicTitle = resHTML.substring(resHTML.indexOf(regexMusic)+regexMusic.length());
                musicTitle = musicTitle.substring(musicTitle.indexOf(regexSongTitle)+regexSongTitle.length());
                musicTitle = musicTitle.substring(0, musicTitle.indexOf("\","));

                musicTitle = musicTitle.replaceAll("amp;", "");
                musicTitle = URLDecoder.decode(musicTitle, "utf-8");

            }

        } catch (IOException e) {
            e.printStackTrace();
            return urlResult;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return urlResult;
        }

        return urlResult+";"+urlTitle+";"+musicTitle;
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

