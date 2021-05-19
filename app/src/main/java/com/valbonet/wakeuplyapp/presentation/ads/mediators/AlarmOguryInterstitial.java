package com.valbonet.wakeuplyapp.presentation.ads.mediators;

import android.app.Activity;

//import com.adincube.sdk.AdinCube;
//import com.adincube.sdk.AdinCubeInterstitialEventListener;


public class AlarmOguryInterstitial {

    private Activity baseActivity;

    private boolean askAgain;
    private boolean isReady;


    public AlarmOguryInterstitial(Activity baseActivity){

        this.baseActivity = baseActivity;

    }

    public void init(){
//		AdinCube.setAppKey("01bf58b0e77d4b7c873b"); //ON LIVE
//        AdinCube.UserConsent.ask(baseActivity);

//        AdinCube.Interstitial.setEventListener(new AdinCubeInterstitialEventListener() {
//            @Override
//            public void onAdCached() {
//                Log.i("Adincube", "ad cached");
//                isReady = true;
//            }
//
//            @Override
//            public void onAdShown() {
//                Log.i("Adincube", "ad cached");
//            }
//
//            @Override
//            public void onError(String s) {
//                Log.i("Adincube", "on error" +s);
//
//                Intent mainActivityIntent;
//                Context applicationContext = baseActivity.getApplicationContext();
//
//                mainActivityIntent = new Intent(applicationContext, ActivityAlarmClock.class);
//                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                applicationContext.startActivity(mainActivityIntent);
//
//            }
//
//            @Override
//            public void onAdClicked() {
//                Log.i("Adincube", "on clicked");
//            }
//
//            @Override
//            public void onAdHidden() {
//                Log.i("Adincube", "ad closed");
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
//        AdinCube.Interstitial.init(baseActivity);

    }

    public void show(){
        //AdinCube.Interstitial.show(baseActivity);
    }

}
