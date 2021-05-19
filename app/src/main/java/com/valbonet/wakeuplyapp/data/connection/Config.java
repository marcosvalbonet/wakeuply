package com.valbonet.wakeuplyapp.data.connection;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class Config {
    public static Boolean configGetVideos;
    public static String jsPLayerUser;
    public static String jsLaunchClickedTikTokVideo;

    public static String getCodeVersion(){
        String codeVersion = new String();
        HttpGetCodeVersion configTask = new HttpGetCodeVersion();
        try {
            String request = configTask.execute().get();
            if (request == null) return codeVersion;

            codeVersion = request;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return codeVersion;
    }

    public static boolean getConfigGetVideos(){
        if (configGetVideos!= null) return configGetVideos;

        configGetVideos = false;
        String configGetVideosValue = new String();
        HttpGetConfigGetVideos configTask = new HttpGetConfigGetVideos();
        try {
            String request = configTask.execute().get();
            if (request != null){

                JSONObject jsonObject = new JSONObject(request);
                int status = jsonObject.getInt("status");
                if (status == 1){
                    configGetVideosValue = jsonObject.getString("info");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        configGetVideos = configGetVideosValue.equals("TRUE");
        return configGetVideos;
    }

    public static String getJSPlayUser(){

        if (jsPLayerUser == null){
            HttpGetJSPlayUser configTask = new HttpGetJSPlayUser();
            try {
                String request = configTask.execute().get();
                jsPLayerUser = request;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return jsPLayerUser;
    }

    public static String getJSLaunchClickedTikTokVideo(){
        if (jsLaunchClickedTikTokVideo == null){
            HttpGetJSPlayOnclickVideo configTask = new HttpGetJSPlayOnclickVideo();
            try {
                String request = configTask.execute().get();
                jsLaunchClickedTikTokVideo = request;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return jsLaunchClickedTikTokVideo;
    }
}
