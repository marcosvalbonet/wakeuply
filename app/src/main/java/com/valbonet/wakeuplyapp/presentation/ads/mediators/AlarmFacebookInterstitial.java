package com.valbonet.wakeuplyapp.presentation.ads.mediators;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.valbonet.wakeuplyapp.presentation.alarmclock.ActivityAlarmClock;
import com.facebook.ads.*;


public class AlarmFacebookInterstitial {

    private String TAG = "FBInterstitial";
    private Activity baseActivity;

    private boolean askAgain;
    private boolean isReady;
    private InterstitialAd interstitialAd;
    Handler handler;
    private boolean hasError;


    public AlarmFacebookInterstitial(Activity baseActivity){

        this.baseActivity = baseActivity;
        handler = new Handler();
        hasError = false;

        AdSettings.addTestDevice("2e562076-6253-42d6-984f-89357827fddf");

    }

    public void init(){
        // Instantiate an InterstitialAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
//        https://developers.facebook.com/docs/audience-network/android
//        Plataforma:	App para Android
//        Formato:	Intersticial
//        Identificador de la ubicación:	701815573545885_701815693545873

//        Wakeup.ly
//        Plataforma:	App para Android
//        Formato:	Intersticial
//        Identificador de la ubicación:	1081269965379435_1081271852045913
//        Herramientas de pruebas
//        Depurador de solicitudes de anuncios: https://business.facebook.com/pub/property/request_debugger?business_id=548753032158470&property_id=262361224696512
//        Administrar dispositivos de prueba: https://business.facebook.com/pub/testdevices?business_id=548753032158470


        interstitialAd = new InterstitialAd(baseActivity, "1081269965379435_1081271852045913");

        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");

                Intent mainActivityIntent;
                Context applicationContext = baseActivity.getApplicationContext();

                mainActivityIntent = new Intent(applicationContext, ActivityAlarmClock.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(mainActivityIntent);
                baseActivity.finish();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());

                hasError = true;

//                Intent mainActivityIntent;
//                Context applicationContext = baseActivity.getApplicationContext();
//
//                mainActivityIntent = new Intent(applicationContext, ActivityAlarmClock.class);
//                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                applicationContext.startActivity(mainActivityIntent);

                //Jump to AlarmOguryInterstitial
//                AlarmOguryInterstitial alarmOguryInterstitial = new AlarmOguryInterstitial(baseActivity);
//                alarmOguryInterstitial.init();
//                alarmOguryInterstitial.show();

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                //interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

    }

    public void show(){

        interstitialAd.show();
    }

    public boolean hasError(){
        return hasError;
    }


    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
    }

}
