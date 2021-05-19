package com.valbonet.wakeuplyapp.data.connection;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.google.gson.Gson;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.utils.UtilsVideo;
import com.valbonet.wakeuplyapp.model.Muser;
import com.valbonet.wakeuplyapp.model.Tiktok;
import com.valbonet.wakeuplyapp.model.Video;
import com.valbonet.wakeuplyapp.presentation.searcher.Lead;
import com.valbonet.wakeuplyapp.presentation.searcher.LeadsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;

public class Data {

    private static ArrayList<Lead> musersList = new ArrayList<Lead>();

    public static void loadTikTokMusers(LeadsAdapter leadsAdapter, int listSize){
        HttpLoadMoreTiktokMusersList muserListTask = new HttpLoadMoreTiktokMusersList(leadsAdapter);
        muserListTask.execute(String.valueOf(listSize));
    }

    public static ArrayList<Lead> getTiktokMusers(int listSize){
        if (musersList.size() == listSize) return musersList;

        HttpGetMoreTiktokMusersList muserListTask = new HttpGetMoreTiktokMusersList();

        try {
            String request =  muserListTask.execute(String.valueOf(listSize)).get();

            if (request != null){

                JSONObject jsonObject = new JSONObject(request);
                int status = jsonObject.getInt("status");
                if (status == 1){
                    JSONArray userObjArray = jsonObject.getJSONArray("info");
                    /*Gson gson = new Gson();
                    List<Lead> leads = (List<Lead>) gson.fromJson(userObjArray.toString(), Lead.class);*/
                    JSONObject userObj;
                    for (int i = 0; i < userObjArray.length(); i++) {
                        userObj = (JSONObject)userObjArray.get(i);
                        String nick = userObj.getString("nickname");
                        String name = userObj.getString("name");
                        String userId = userObj.getString("userId");
                        String linkUrl = userObj.getString("url");
                        String imgSrc = userObj.getString("imgSrc");
                       // String imgage = userObj.getString("imgage");

                        musersList.add(new Lead(nick, name, userId, linkUrl, imgSrc));
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return musersList;
    }

    public static ArrayList<Lead> findTiktokMusersByNickname(String nickname){
        ArrayList<Lead> musersFoundList = new ArrayList<Lead>();
        HttpGetTiktokMuserList tiktokMuserListTask = new HttpGetTiktokMuserList();
        try {
            String request = tiktokMuserListTask.execute(nickname).get();

           if (request != null && !request.isEmpty()) {

                JSONObject jsonObject = new JSONObject(request);
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

                        musersFoundList.add(new Lead(nick, name, userId, linkUrl, imgSrc));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return musersFoundList;
    }

    public static boolean addNewTiktokMuser(String usrNicknameValue, String usrNameValue, String urlUser, String imgURL) {
        HttpAddNewTiktokMuser addMuserTask = new HttpAddNewTiktokMuser();
        try {
            String request = addMuserTask.execute(usrNicknameValue, usrNameValue, urlUser, Utils.reviewURL(imgURL)).get();

            JSONObject jsonObject = new JSONObject(request);
            int status = jsonObject.getInt("status");
            if (status == 1){
                return true;
                //Toast.makeText(getApplicationContext(), R.string.user_added_msg, Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(getApplicationContext(), "User was already added", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateTiktokMuserImage(String usrNicknameValue, String imgURL) {
        HttpUpdateTiktokMuserImg tiktokImgUrlTask = new HttpUpdateTiktokMuserImg();
        tiktokImgUrlTask.execute(usrNicknameValue, Utils.reviewURL(imgURL));
    }

    public static void updateTiktokMuserName(String usrNicknameValue, String usrNameValue) {
        HttpUpdateNameTiktokMuser updateName = new HttpUpdateNameTiktokMuser();
        updateName.execute(usrNicknameValue, usrNameValue);
    }

    public static void updateMuserUserId(String nickUser, String userId) {
        HttpUpdateUserIdTiktokMuser updateName = new HttpUpdateUserIdTiktokMuser();
        updateName.execute(nickUser, userId);
    }

    public static void addTiktokMuserVideos(String nickUser, ArrayList<String> previewImgVideos, ArrayList<String> pageVideoUrl) {
        HttpAddNewTiktokVideos addVideosTask = new HttpAddNewTiktokVideos(previewImgVideos, pageVideoUrl);
        addVideosTask.execute(nickUser);
    }

    public static Observable<List<Tiktok>> getRandomTiktoks(int nbMusers){
        List<Tiktok> randomTiktoks = new ArrayList<>();
        List<Tiktok> tiktoks = getRandomTiktokMusers(nbMusers);
        for(Tiktok t : tiktoks){
            if(
                t.getVideo().getVideoName().contains("private account") ||
                t.getVideo().getVideoName().contains("video is unavailable")){
                return getRandomTiktoks(nbMusers);
            }else{
                randomTiktoks.add(t);
            }

        }
        return Observable.just(randomTiktoks);
    }

    public static List<Tiktok> getRandomTiktokMusers(int nbMusers) {
        HttpGetRandomMusers getRandomMusersTask = new HttpGetRandomMusers();

        ArrayList<Tiktok> tiktoks = new ArrayList<>();
        try {

            String musersResponse = getRandomMusersTask.execute(String.valueOf(nbMusers)).get();
            if (musersResponse == null) return tiktoks;

            JSONObject jsonObject = new JSONObject(musersResponse);
            int status = jsonObject.getInt("status");
            if (status == 1){
                JSONArray userObjArray = jsonObject.getJSONArray("info");
                JSONObject userObj;
                for (int i = 0; i < userObjArray.length(); i++) {
                    userObj = (JSONObject)userObjArray.get(i);
                    String nick = userObj.getString("nickname");
                    String name = userObj.getString("name");
                    String linkUrl = userObj.getString("url");
                    String imgSrc = userObj.getString("imgSrc");
                    String videoUrlPage = userObj.getString("video");

                    Muser muser = new Muser(nick, name, imgSrc, linkUrl, false);
                    Video video = getVideo(videoUrlPage);
                    if (video != null && video.getVideoURL() != null){
                        tiktoks.add(new Tiktok(muser,  video));
                        //System.out.println("New video added: "+videoUrlPage);
                    }
                }
            }

            return tiktoks;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRandomTiktokMuserVideos(String nickUser) {
        HttpGetRandomTiktokVideos getRandomVideosTask = new HttpGetRandomTiktokVideos();

        try {
            String request = getRandomVideosTask.execute(nickUser).get();

            JSONObject jsonObject = new JSONObject(request);
            int status = jsonObject.getInt("status");
            if (status == 1){
                return jsonObject.getString("info");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getVideoFile(Context context, String urlVideoPage){
        if (urlVideoPage == null) return null;

        File outputFile = null;
        try {
            File outputDir = context.getCacheDir(); // context being the Activity pointer
            outputFile = File.createTempFile("temp", "mp4", outputDir);

            //File folder = context.getFilesDir();

            File folder = new File (Environment
                    .getExternalStorageDirectory() + "/Download");
            if (folder.mkdirs()){
                System.console().printf("Problems to create folder");
            }
            outputFile = new File(folder, "myData.mp4");

            HttpGetTiktokVideoFile  videoFile = new HttpGetTiktokVideoFile(urlVideoPage, outputFile);
            String isVideoCreated = videoFile.execute().get();
            if (isVideoCreated != null) return null;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return outputFile;
    }
    public static String getUrlVideo(String urlVideoPage) {
        if (urlVideoPage == null) return null;

        String videoUrl = new String();
//        String url= "https://www.tiktok.com/@baddalii/video/6828455429387848966";
        HttpGetTiktokVideoURL video = new HttpGetTiktokVideoURL(urlVideoPage);

        try {
            videoUrl = video.execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return videoUrl;
    }

    public static Video getVideo(String urlVideoPage){
        if (urlVideoPage == null) return null;

        String response = new String();
        Video video = null;
//        String url= "https://www.tiktok.com/@baddalii/video/6828455429387848966";
        String cookie = UtilsVideo.createCookieVideo();
        HttpGetTiktokVideo videoTask = new HttpGetTiktokVideo(urlVideoPage, cookie);

        try {
            response = videoTask.execute().get();
            if (response == null) return null;

            String [] videoParams = response.split(";");
            video = new Video(urlVideoPage, videoParams[0], videoParams[1], cookie);
            if (videoParams.length > 2 && !videoParams[2].isEmpty())
                video.setMusicName(videoParams[2]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return video;
    }

    /**
     * It doesn´t work. Not exists tac= in this url
     * @return
     */
    public static String getExtractTac(){
        String tac = new String();
        //https://www.tiktok.com/discover
        HttpGetTiktokExtractTac extractTacTask = new HttpGetTiktokExtractTac();

        try {
            tac = extractTacTask.execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return tac;
    }


    public static String getUrlVideoNoWatermark(String urlVideo) {
        String videoUrl = new String();
        String videoID = new String();
//        String url= "https://www.tiktok.com/@baddalii/video/6828455429387848966";
        HttpGetTiktokVideoURLNoWatermark video = new HttpGetTiktokVideoURLNoWatermark(urlVideo);

        try {
            videoID = video.execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (!videoID.isEmpty()){
           // videoUrl = "https://api2-16-h2.musical.ly/aweme/v1/play/?video_id="+videoID+"&vr_type=0&is_play_url=1&source=PackSourceEnum_PUBLISH&media_type=4";
            videoUrl = "https://api.tiktokv.com/aweme/v1/play/?video_id="+videoID+"&line=0&ratio=720p&watermark=0&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0&logo_name=tiktok";
        }
        return videoUrl;
    }

}
