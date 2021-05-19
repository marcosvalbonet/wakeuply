package com.valbonet.wakeuplyapp.data.connection;

import android.os.AsyncTask;

import com.valbonet.wakeuplyapp.Constants;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class HttpAddNewTiktokVideos extends AsyncTask<String, Void, String> {

    HttpPost httppost;
    DefaultHttpClient httpclient;
    String URL = Constants.baseURL + "tiktokVideos.php";

    ArrayList<String> imgPrevVideos;
    ArrayList<String> pageVideos;

   public HttpAddNewTiktokVideos(ArrayList<String> imgPrevVideos, ArrayList<String> pageVideos){
        this.imgPrevVideos = imgPrevVideos;
        this.pageVideos = pageVideos;
    }

    @Override
    protected String doInBackground(String... params) {
        String request = null;

        httpclient = new DefaultHttpClient();
        httppost = new HttpPost(URL);
        httppost.setHeader("Accept-Charset","utf-8");

        if (imgPrevVideos == null || pageVideos == null) return null;

        int size = imgPrevVideos.size() + pageVideos.size() + 2;

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(size);
        nameValuePairs.add(new BasicNameValuePair("function", "addNewVideos"));
        nameValuePairs.add(new BasicNameValuePair("nickname", params[0]));

        for (int i=0; i < imgPrevVideos.size(); i++) {
            if (pageVideos.get(i) == null) {
                i = imgPrevVideos.size();
                break;
            }else {
                nameValuePairs.add(new BasicNameValuePair("preview"+i, imgPrevVideos.get(i)));
                nameValuePairs.add(new BasicNameValuePair("video"+i, pageVideos.get(i)));
            }

        }

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            request = httpclient.execute(httppost, responseHandler);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;
    }

}

