package com.valbonet.wakeuplyapp.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.data.connection.Config;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.usecases.GetUserIdUseCase;
import com.valbonet.wakeuplyapp.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class PlayUserWebActivity extends AppCompatActivity {

    String title;
    String url;

    private ActionBar actionBar;

    private String urlImg;
    String nickUser;
    String nameUser;
    String userId;
    private String usrNicknameValue;
    private String usrNameValue;
    private String urlUser;
    private boolean isNewURL;
    private boolean isNewUrlMuser;
    String bundleUserImg;
    private String videoName;

    WebView mWebview;

    private String TAG = "PlayUserWebActivity";

    ArrayList<String> previewImgVideos; //URLS de los videos para tag video
    ArrayList<String> pageVideoUrl; //URLS de las pagina de un video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playuser2);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = this.getIntent().getExtras();
        title = "Wake";

        nameUser = bundle.getString("nameUser");
        nickUser = bundle.getString("nickUser");
        urlUser = bundle.getString("urlUser");
        isNewURL = bundle.containsKey("isNewURL")? bundle.getBoolean("isNewURL") : false;
        isNewUrlMuser = bundle.containsKey("isNewURL")? bundle.getBoolean("isNewURL") : false;
        bundleUserImg = bundle.getString("userImgURL");

        actionBar.setTitle(nameUser);

        //mWebview  = new WebView(this);
        mWebview = (WebView)findViewById(R.id.wakeup_webview);
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebview.getSettings().setDefaultTextEncodingName("utf-8");
        mWebview.setInitialScale(1);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setBuiltInZoomControls(false);
        mWebview.requestFocus(View.FOCUS_DOWN);
        mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick");
            }
        });

        mWebview.setOnTouchListener(new View.OnTouchListener() {

            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;

            private int fingerState = FINGER_RELEASED;

            String events = new String();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, String.valueOf(event.getAction()));
                events += event.getAction() ;
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) fingerState = FINGER_DRAGGING;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (events.equals("01") || events.equals("021") || events.equals("0221")
                                || events.equals("02221") || events.equals("022221")) {
                            //01 MotionEvent.ACTION_DOWN and MotionEvent.ACTION_UP
                            //021 MotionEvent.ACTION_DOWN MotionEvent.ACTION_MOVE and MotionEvent.ACTION_UP
                            //0221 MotionEvent.ACTION_DOWN MotionEvent.ACTION_MOVE MotionEvent.ACTION_MOVE and MotionEvent.ACTION_UP
//                        if(fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;

                            Log.d(TAG, "ontouch up");

                            // Your onClick codes
                            new CountDownTimer(200, 100) {

                                public void onTick(long millisUntilFinished) {
                                    Log.println(Log.VERBOSE, TAG, "seconds remaining: " + millisUntilFinished / 1000);
                                }

                                public void onFinish() {
                                    //Get the info of card/view and launch the play Video
                                    launchClickedTikTokVideo();
                                }
                            }.start();

                        }
                        else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                        else fingerState = FINGER_UNDEFINED;
                        events = "";
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;

                }

                return false;
            }
        });

        mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		mWebview.getSettings().setUserAgentString(Constants.userAgent);

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String js = Config.getJSPlayUser();
//                "var body = document.getElementsByTagName(\"body\"); if(body.length >0){ body[0].style.width = \"640px\";} " +
//                String js = "(function(){ " +
//                        "var appRoot = document.getElementById(\"__next\"); if(appRoot){ appRoot.style.marginTop = \"-57px\"; appRoot.style.width=\"auto\";}	 " +
//                        "var title = document.getElementsByClassName(\"header-container\"); if(title.length >0){title[0].style.display = \"none\";} " +
//                        "var sideBarContainer = document.getElementsByClassName(\"side-bar-container\"); if(sideBarContainer.length >0){sideBarContainer[0].style.display = \"none\";} " +
//                        "setTimeout(hideFollowButton, 500); function hideFollowButton (){var shareFollow = document.getElementsByClassName(\"follow-button\"); if(shareFollow.length >0){shareFollow[0].style.display = \"none\";} }" +
//                        "})();";

                mWebview.evaluateJavascript(js,
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {

                            }
                        });
            }
        });

        mWebview.loadUrl(urlUser);


        mWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {

                if(title != mWebview.getTitle()){//getTitle has the newer Title
                    // get the Title
                    title = mWebview.getTitle();
                    //setTitle(title);
                }

            }});

        setContentView(mWebview);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mWebview.setVisibility(View.VISIBLE);

        if (userId == null){
            if (!isNewURL){
                //updateUserId();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Config.getConfigGetVideos()){
            Data.addTiktokMuserVideos(nickUser, previewImgVideos, pageVideoUrl);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateUserId(){
        GetUserIdUseCase getUserIdUseCase = new GetUserIdUseCase();
        try {
            String response = getUserIdUseCase.execute(nickUser).get();
            String[] ids = response.split(";");
            if (ids.length>1) {
                userId = ids[0];
                String secUid = ids[1];
                if (!isNewUrlMuser) {
                    Data.updateMuserUserId(nickUser, userId);
                    //TODO updateMuserSecUid(nickUser, secUid);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private int findVideoPage(String urlImgToSearch, String[] videoPlayerUrlsParts) {

        for(int i=0; i<videoPlayerUrlsParts.length; i++){
            if (videoPlayerUrlsParts[i] !=null &&
                    videoPlayerUrlsParts[i].contains(urlImgToSearch)){
                return i;
            }
        }
        return -1;
    }

    private String getUrlVideo(Document doc){
        String urlVideo = "";
        Elements videos = doc.select("video.video-player");
        for (Element video : videos){
            urlVideo = video.attr("src");
        }
        return urlVideo;
    }

    private String getUrlPage(Document doc, String[] videoPlayerUrlsArray, String[] videoUrlPagesArray){
        String urlImgToSearch = null;
        String urlPageValue = null;
        Elements bgImage = doc.select("div.video-card-browse");
        for (Element div : bgImage){
            String attr = div.attr("style");
            if (!attr.isEmpty()){
                String imgSRC = attr.substring( attr.indexOf("https://"), attr.indexOf(")") );
                urlImgToSearch = imgSRC.replace("\"", "");
            }
        }

        //TODO: code to get the urlPage
        if (urlImgToSearch != null){
            int positionPage = findVideoPage(urlImgToSearch, videoPlayerUrlsArray);
            if (positionPage != -1){
                urlPageValue =  videoUrlPagesArray[positionPage];
            }
        }
        return urlPageValue;
    }

    private String getVideoName(Document doc){
        String name = new String();
        Elements videoNames = doc.select("h1.video-meta-title");
        if (!videoNames.isEmpty()){
            name = videoNames.get(0).html();
            try{
                if (name.startsWith("<strong>")){
                    name = name.substring(7, name.indexOf("</strong>"));
                }else{
                    name = name.substring(0, name.indexOf("</strong>"));
                }
            }catch(Exception e){
                Log.d(TAG, e.getMessage());
            }

        }
        return name;
    }

    private String getTiktokerNickName(Elements userInfoContent){
        String nickNameValue = new String();
        if (!userInfoContent.isEmpty()){
            Elements usrNickname = userInfoContent.get(0).select("h2.user-username");
            if (!usrNickname.isEmpty()){
                try{
                    nickNameValue = usrNickname.get(0).html();
                    if (nickNameValue==null){
                        nickNameValue = nickUser;
                    }
                    if (!nickNameValue.startsWith("@")){
                        nickNameValue = "@" + nickNameValue;
                    }
                    if (nickNameValue.contains("<")){
                        nickNameValue = nickNameValue.substring(0, nickNameValue.indexOf("<"));
                    }
                    if (nickNameValue.contains("\n")){
                        nickNameValue = nickNameValue.substring(0, nickNameValue.indexOf("\n"));
                    }
                }catch(Exception e){
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        if (nickUser == null) nickUser = nickNameValue;
        return nickNameValue;
    }

    private String getTiktokerName(Elements userInfoContent){
        String nameValue = new String();
        Elements usrName = userInfoContent.get(0).select("h2.user-nickname");

        if (usrName != null && !usrName.isEmpty()) {
            try{
                nameValue = usrName.get(0).html();
                nameValue = nameValue.substring(0, nameValue.indexOf("<span"));
            }catch(Exception e){
                Log.d(TAG, e.getMessage());
            }
        }
        if (nameValue == null) {
            nameValue = nameUser;
        }
        return nameValue;
    }

    private String getTiktokerImageUrl(Document doc){
        Elements images = doc.select("img.avatar-wrapper");
        String imgValue = new String();
        for (Element img : images){
            imgValue = img.attr("src");
        }

        if (imgValue.isEmpty()){
            Elements spans = doc.select("span.tiktok-avatar");
            for (Element span : spans){
                images = span.select("img");
                for (Element img : images){
                    imgValue = img.attr("src");
                }
            }
        }

        if (imgValue.isEmpty()) return bundleUserImg;

        imgValue = imgValue.replace("&amp", "&");
        return imgValue;
    }


    private void updateTiktokerName(){
        //Si el nick encontrado en la pagina del video no es el mismo,
        //no actualizamos
        if (!usrNicknameValue.equals(nickUser)) return;
        // Si el usuario no está en bbdd, no tenemos nameUser todavia
        if (nameUser == null) return;

        if (!usrNameValue.isEmpty()){
            if (!nameUser.contains(usrNameValue)) {
                Data.updateTiktokMuserName(usrNicknameValue, usrNameValue);
            }
        }
    }

    private void updateTiktokerImg(){
        //Si el nick encontrado en la pagina del video no es el mismo,
        //no actualizamos
        if (!usrNicknameValue.equals(nickUser)) return;

        if (!urlImg.isEmpty()){
            // Si la urlImg es diferente a la imagen que tenemos
            // y no estamos ingresanddo una url usuairo nuevo, actualizamos en BBDD
            if(bundleUserImg!=null && !bundleUserImg.contains(urlImg) && !isNewURL){
                Data.updateTiktokMuserImage(usrNicknameValue, urlImg);
            }
        }else{
            urlImg = bundleUserImg;
        }
    }

    private void launchClickedTikTokVideo() {
//        String js = "(function(){ " +
//                "var videoURL; var cardVideos = document.getElementsByClassName(\"video-card-big\"); if(cardVideos.length >0){videoURL = cardVideos[0].outerHTML; cardVideos[0].style.display = \"none\";} " +
//                "var videoPlayer = document.getElementsByClassName(\"video-player\"); if(videoPlayer.length >0){ for(var i=0; i < videoPlayer.length ; i++){videoPlayer[i].pause();}} " +
//                "var body = document.getElementsByTagName(\"body\"); if(body.length >0){ body[0].style.overflow = \"auto\"; body[0].style.paddingRight = \"0px\";} " +
//                "var copyLink = document.getElementsByClassName(\"copy-link-container\"); if(copyLink.length >0){var inputTagName = copyLink[0].getElementsByTagName(\"input\"); if( inputTagName.length >0){urlPage = inputTagName[0].value;} }" +
//                "var videoUrlPages = ''; var urlPages = document.getElementsByClassName(\"video-feed-item-wrapper\"); if(urlPages.length >0){ for (const prop in urlPages) { videoUrlPages += urlPages[prop]+\"#*#\"; } }" +
//                "var videoplayerUrls = ''; var videoUrls = document.getElementsByClassName(\"image-card\"); if(videoUrls.length >0){ for (var i = 0; i < videoUrls.length; i++) { videoplayerUrls += videoUrls[i].style.backgroundImage+\"#*#\"; } }" +
//                "return videoURL+\"#;#\"+videoUrlPages+\"#;#\"+videoplayerUrls;})();";

        String js = Config.getJSLaunchClickedTikTokVideo();



        ValueCallback<String> callback = new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        Log.d("HTML", html);
                        if (!html.equals("null")){

                            String[] parts = html.split("#;#");
                            String videoCard = parts[0];
                            String videoUrlPages = parts[1]; //URLS de las pagina de un video
                            String videoPlayerUrls = parts[2]; //URLS de los videos para tag video

                            if (!videoCard.isEmpty() && !videoCard.equals("\"undefined")){

                                mWebview.setVisibility(View.GONE);

                                String newHTML = videoCard.replaceAll("u003C", "<");
                                //newHTML = StringEscapeUtils.unescapeJava(newHTML);
                                videoCard = newHTML.replaceAll("\\\\", new String());

                                Document doc = Jsoup.parse(videoCard);
                                String[] videoUrlPagesArray = videoUrlPages.split("#*#");
                                String[] videoPreviewImages = videoPlayerUrls.split("#*#");

                                addVideos(videoPreviewImages, videoUrlPagesArray);

                                // tiktoks' url video
                                String urlVideo = getUrlVideo(doc);

                                // User url tiktoks' page
                                String urlPage = getUrlPage(doc, videoPreviewImages, videoUrlPagesArray);

                                //User Nickname & name
                                Elements userInfoContent = doc.select("div.user-info-container");
                                usrNicknameValue = getTiktokerNickName(userInfoContent);
                                usrNameValue = getTiktokerName(userInfoContent);
                                updateTiktokerName();

                                //User Image
                                urlImg = getTiktokerImageUrl(doc);
                                updateTiktokerImg();

                                // tiktoks' video name
                                videoName = getVideoName(doc);

                                if (isNewURL && urlUser != null){
                                    Data.addNewTiktokMuser(usrNicknameValue, usrNameValue, urlUser, Utils.reviewURL(urlImg));
                                    isNewURL = false;
                                }

                                if(!urlVideo.isEmpty()){
                                    final Intent myIntent;
//                                    if (Constants.TEST){
                                        myIntent = new Intent(getApplicationContext(), PlayVideoPagerAdapterActivity.class);
//                                    } else {
//                                        myIntent = new Intent(getApplicationContext(), PlayVideoActivity.class);
//                                    }
                                    myIntent.putExtra(Constants.imgUser, urlImg);
                                    if (usrNicknameValue != null) {
                                        myIntent.putExtra(Constants.muserNickname, usrNicknameValue);
                                    }
                                    if (videoName != null) myIntent.putExtra(Constants.videoName, videoName);
                                    myIntent.putExtra(Constants.videoURLPage, urlPage);
                                    myIntent.putExtra(Constants.videoURL, urlVideo);
                                    myIntent.putExtra(Constants.muserURL, urlUser);
                                    myIntent.putExtra(Constants.isNewUrlMuser, isNewUrlMuser);
                                    if (getIntent().getExtras()!= null && getIntent().getExtras().containsKey("alarmID")){
                                        myIntent.putExtra("alarmID", getIntent().getExtras().getLong("alarmID"));
                                    }
//                                    if (Constants.TEST){
                                        myIntent.putStringArrayListExtra(Constants.videoURLPages, getArrayVideoUrlPages(videoUrlPagesArray, urlPage));
//                                    }
                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(myIntent);
                                }
                            }

                        }
                    }
                };

        mWebview.evaluateJavascript(js, callback);
    }

    private void addVideos(String[] videoPreviewImages, String[] videoUrlPages) {
        previewImgVideos = new ArrayList<String>();
        pageVideoUrl = new ArrayList<String>();
        for(String previewImg : videoPreviewImages){
            //if start by http
            if (previewImg.contains("http")){
                String imgSRC = previewImg.substring( previewImg.indexOf("https://"), previewImg.indexOf(")") );
                urlImg = imgSRC.replace("\"", "");
                urlImg = urlImg.replace("\\", "");
                if (urlImg.startsWith("http"))
                    previewImgVideos.add(urlImg);
            }
        }
        for(String videoUrl : videoUrlPages){
            //if start by http
            if (videoUrl.startsWith("http")){
                if (pageVideoUrl.size() >= previewImgVideos.size()){
                    return;
                }
                pageVideoUrl.add(videoUrl);
            }
        }
    }

    private ArrayList<String> getArrayVideoUrlPages(String[] videoUrlPages, String firstUrlPage){
        ArrayList<String> pageVideoUrl = new ArrayList<String>();
        Boolean isFirstUrlPageFound = false;
        for(String videoUrl : videoUrlPages){
            //if start by http
            if (videoUrl.startsWith("http")){
                isFirstUrlPageFound = isFirstUrlPageFound || videoUrl.equals(firstUrlPage);
                if(isFirstUrlPageFound){
                    pageVideoUrl.add(videoUrl);
                }
            }
        }
        return pageVideoUrl;
    }
}
