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


public class HttpUpdateUserIdTiktokMuser extends AsyncTask<String, Void, String> {

    HttpPost httppost;

    DefaultHttpClient httpclient;
    String URL = Constants.baseURL + "tiktokMusers.php";

    @Override
    protected String doInBackground(String... params) {
        String request = null;

        httpclient = new DefaultHttpClient();
       // httppost = new HttpPost("https://www.wakeupincomes.com/wakeuply/tiktokMusers.php");
        httppost = new HttpPost(URL);
        httppost.setHeader("Accept-Charset","utf-8");

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("function", "updateUserIdTikTokMuser"));
        nameValuePairs.add(new BasicNameValuePair("nickname", params[0]));
        nameValuePairs.add(new BasicNameValuePair("userId", params[1]));

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

