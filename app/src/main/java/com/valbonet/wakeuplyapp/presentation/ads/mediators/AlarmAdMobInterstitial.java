package com.valbonet.wakeuplyapp.presentation.ads.mediators;

import android.app.Activity;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;


public class AlarmAdMobInterstitial {

    private Activity baseActivity;

    private boolean askAgain;
    private boolean isReady;

//    InterstitialAd mInterstitialAd;


    public AlarmAdMobInterstitial(Activity baseActivity){

        this.baseActivity = baseActivity;

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
//        MobileAds.initialize(baseActivity, "ca-app-pub-5854207430120293~3905252542");
//        mInterstitialAd = new InterstitialAd(baseActivity);
//        mInterstitialAd.setAdUnitId("ca-app-pub-5854207430120293/3672777622");

    }

    public void init(){

//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                Log.i("AdMob", "ad cached");
//                isReady = true;
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//                Log.i("AdMob", "failed to load");
//
//                // Load the next interstitial.
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when when the interstitial ad is closed.
//                Log.i("AdMob", "ad closed");
//
//
//                Intent mainActivityIntent;
//                Context applicationContext = baseActivity.getApplicationContext();
//
//                mainActivityIntent = new Intent(applicationContext, ActivityAlarmClock.class);
//                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                applicationContext.startActivity(mainActivityIntent);
//                baseActivity.finish();
//            }
//        });
//
//        AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//        mInterstitialAd.loadAd(request);

    }

    public void show(){

//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }else{
//            Intent mainActivityIntent;
//            Context applicationContext = baseActivity.getApplicationContext();
//
//            mainActivityIntent = new Intent(applicationContext, ActivityAlarmClock.class);
//            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            applicationContext.startActivity(mainActivityIntent);
//            baseActivity.finish();
//        }
    }

}
