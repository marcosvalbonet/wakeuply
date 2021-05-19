package com.valbonet.wakeuplyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    // references to our images
    private ArrayList<String> mThumbIds;
    private ArrayList<String> videosURL;

    public ImageAdapter(Context c) {
        mContext = c;
        mThumbIds = new ArrayList();
        videosURL = new ArrayList();
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(500, 700));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        String urlIMG = mThumbIds.get(position);
        DownloadImageTask loadImg = new DownloadImageTask(imageView);
        loadImg.execute(urlIMG);
        return imageView;
    }

    public void setThumbs(ArrayList<String> imgArray){
        mThumbIds.addAll(imgArray);
    }

    public String getThumbs(int position){
        return mThumbIds.get(position);
    }

    public void setVideosURL(ArrayList<String> videosUrl){
        videosURL.addAll(videosUrl);
    }

    public String getVideosURL(int position){
        return videosURL.get(position);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
