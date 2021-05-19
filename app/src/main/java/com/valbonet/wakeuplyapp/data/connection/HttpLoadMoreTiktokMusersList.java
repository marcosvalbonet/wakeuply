package com.valbonet.wakeuplyapp.data.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.presentation.searcher.Lead;
import com.valbonet.wakeuplyapp.presentation.searcher.LeadsAdapter;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class HttpLoadMoreTiktokMusersList extends AsyncTask<String, Void, String> {

    HttpPost httppost;
    DefaultHttpClient httpclient;
    String URL = Constants.baseURL + "tiktokMusers.php";

    private LeadsAdapter leadsAdapter;

    public HttpLoadMoreTiktokMusersList(LeadsAdapter leadsAdapter) {
        this.leadsAdapter = leadsAdapter;
    }

    @Override
    protected String doInBackground(String... params) {
        String request = null;

        httpclient = new DefaultHttpClient();
        //httppost = new HttpPost("http://www.wakeupincomes.com/wakeuply/tiktokMusers.php");
        httppost = new HttpPost(URL);

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("function", "getMoreTikTokMusersList"));
        nameValuePairs.add(new BasicNameValuePair("size", params[0]));

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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

    @Override
    protected void onPostExecute(String response) {
        ArrayList<Lead> musersList = new ArrayList<Lead>();
        try {

            if (response != null){

                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");
                if (status == 1){
                    JSONArray userObjArray = jsonObject.getJSONArray("info");
                    JSONObject userObj;
                    for (int i = 0; i < userObjArray.length(); i++) {
                        userObj = (JSONObject)userObjArray.get(i);
                        String nick = userObj.getString("nickname");
                        String name = userObj.getString("name");
                        String userId = userObj.getString("userId");
                        String linkUrl = userObj.getString("url");
                        String imgSrc = userObj.getString("imgSrc");

                        musersList.add(new Lead(nick, name, userId, linkUrl, imgSrc));
                    }
                    this.leadsAdapter.refreshLeads(musersList);
                }
            }else{
                Context context = this.leadsAdapter.getContext();
                Toast.makeText(context,"Error conectando con servidor. Revisa tu conexión", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

