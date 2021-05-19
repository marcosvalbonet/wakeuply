package com.valbonet.wakeuplyapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;


public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
    private static HashMap<String, Bitmap> imagesLoaded = new HashMap<String, Bitmap>();
    ImageView bmImage;
    String username;

    HttpPost httppost;
    DefaultHttpClient httpclient;
    String URL = Constants.baseURL + "tiktokMusers.php";

    public LoadImageTask(ImageView bmImage, String username) {
        this.bmImage = bmImage;
        this.username = username;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bmp = null;

        if(imagesLoaded.containsKey(urldisplay)){
            bmp = imagesLoaded.get(urldisplay);
        }else{
            try {
                httpclient = new DefaultHttpClient();
                //httppost = new HttpPost("http://www.wakeupincomes.com/wakeuply/tiktokMusers.php");
                httppost = new HttpPost(URL);

                List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("function", "getTikTokMuserImage"));
                nameValuePairs.add(new BasicNameValuePair("nickname", username));

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String requestImageBlob = httpclient.execute(httppost, responseHandler);

                if (!requestImageBlob.equals("")){
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                        byte[] bytesImage = Base64.getDecoder().decode(requestImageBlob);
                        InputStream is = new ByteArrayInputStream(bytesImage);
                        bmp = BitmapFactory.decodeStream(is);
                    }
                }

                if (bmp == null){
                    InputStream in = new URL(urldisplay).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                }

                imagesLoaded.put(urldisplay, bmp);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return BitmapFactory.decodeResource(bmImage.getResources(), R.drawable.wakeuply);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                return BitmapFactory.decodeResource(bmImage.getResources(), R.drawable.wakeuply);
            }
        }
        return bmp;
    }
    protected void onPostExecute(Bitmap result) {
        try{
            Glide.with(bmImage.getContext()).asBitmap().load(result).into(bmImage);
        }catch(Exception e){
            bmImage.setImageBitmap(result);
        }

    }

}
