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
public class HttpGetTiktokVideoURL extends AsyncTask<String, Void, String> {

    HttpGet httpget;
    DefaultHttpClient httpclient;
    private String URL;
    private HttpResponse response;

    public HttpGetTiktokVideoURL(String videoPageURL){
        URL = videoPageURL;
    }

    @Override
    protected String doInBackground(String... params) {
        String urlResult = null;

        httpclient = new DefaultHttpClient();
        httpget = new HttpGet();

        try {
            URI website = new URI(URL);
            httpget.setURI(website);
            response = httpclient.execute(httpget);

//            URI url2 = new URI("https://v16-web.tiktok.com/video/tos/useast2a/tos-useast2a-ve-0068c004/2cc22eedd86d40e6a750b028e50bac2d/?a=1988&br=2546&bt=1273&cr=0&cs=0&cv=1&dr=0&ds=3&er=&expire=1603816939&l=202010271042040101891940810D00FF7B&lr=tiktok_m&mime_type=video_mp4&policy=2&qs=0&rc=ajY4bTxsOXB5djMzZTczM0ApOTQ2ZTdmOjtkNzU3Ozc3aGdoLjBlLTVucTRfLS1gMTZzc2JfYC1fYzM0MTFjYzIyM2I6Yw%3D%3D&signature=36f78ac9cf7cd40ff20105bcf7033922&tk=tt_webid_v2&vl=&vr=");
//            String URL2 = url2.toString();
//            java.net.URL urlToConnect = new URL(URL2);
            java.net.URL urlToConnect = new URL(URL);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToConnect.openConnection();
            String myCookie = "tt_webid_v2=6812345678914" ;
            urlConnection.setRequestProperty("Cookie", myCookie);
           // urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            urlConnection.setRequestProperty("REFERER", "https://www.tiktok.com/");

            InputStream inS = new BufferedInputStream(urlConnection.getInputStream());
            String resHTML = readStream(inS);
            if (resHTML == null) return null;

            //Get Video
            String regex = "property=\"og:video\" content=\"";
            String regex2 = "playAddr";

             if(resHTML.indexOf(regex)!= -1)   {
                 urlResult = resHTML.substring(resHTML.indexOf(regex)+regex.length());
                 urlResult = urlResult.substring(0, urlResult.indexOf("\""));

                 urlResult = urlResult.replaceAll("amp;", "");
                 urlResult = URLDecoder.decode(urlResult, "utf-8");

             } else if(resHTML.indexOf(regex2)!= -1)   {
                 urlResult = resHTML.substring(resHTML.indexOf(regex2)+regex2.length()+3);
                 urlResult = urlResult.substring(0, urlResult.indexOf("\""));

                 urlResult = urlResult.replaceAll("\u0026", "&");
                 urlResult = urlResult.replaceAll("\\u0026", "&");
                 urlResult = urlResult.replaceAll("\\\u0026", "&");
                 urlResult = urlResult.replaceAll("\\\\u0026", "&");
                 urlResult = URLDecoder.decode(urlResult, "utf-8");

            }else{
                Log.e("NoURLVideo", "Video Privado");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
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

