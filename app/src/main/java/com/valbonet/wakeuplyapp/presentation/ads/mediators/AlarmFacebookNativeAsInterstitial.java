package com.valbonet.wakeuplyapp.presentation.ads.mediators;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.presentation.alarmclock.ActivityAlarmClock;

import java.util.ArrayList;
import java.util.List;


public class AlarmFacebookNativeAsInterstitial {

    private String TAG = AlarmFacebookNativeAsInterstitial.class.getSimpleName();
    private Activity baseActivity;

    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;
    Handler handler;
    private boolean hasError;



    public AlarmFacebookNativeAsInterstitial(Activity baseActivity){

        this.baseActivity = baseActivity;
        handler = new Handler();
        hasError = false;

        //To test Facebook ads
        if (Constants.TEST){
            AdSettings.addTestDevice("2e562076-6253-42d6-984f-89357827fddf");
        }
    }

    public void init(){
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).


//        https://developers.facebook.com/docs/audience-network/android
//        Plataforma:	App para Android
//        Formato:	Native
//        Identificador de la ubicación:	701815573545885_701815693545873

//        Wakeup.ly
//        Plataforma:	App para Android
//        Formato:	Native
//        Identificador de la ubicación:	1081269965379435_1388133431359752
//        Herramientas de pruebas
//        Depurador de solicitudes de anuncios: https://business.facebook.com/pub/property/request_debugger?business_id=548753032158470&property_id=262361224696512
//        Administrar dispositivos de prueba: https://business.facebook.com/pub/testdevices?business_id=548753032158470


        nativeAd = new NativeAd(baseActivity, "1081269965379435_1388133431359752");

        nativeAd.setAdListener(new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                hasError = true;

//                Intent myIntent = new Intent(baseActivity, ActivityAlarmClock.class);
//                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                baseActivity.startActivity(myIntent);
//                baseActivity.finish();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");

                // Inflate Native Ad into Container
//                inflateAd(nativeAd);

                // Inflate Native As Interstitial Ad into Container
//                inflateInterstitialAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }

        });

        // Request an ad
        nativeAd.loadAd();

    }

    public void show(){

        inflateInterstitialAd(nativeAd);
//        nativeAd.show();
    }



    public boolean hasError(){
        return hasError;
    }


    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = baseActivity.findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(baseActivity);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        //adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        adView = (LinearLayout) inflater.inflate(R.layout.interstitial_ad_layout_test, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = baseActivity.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(baseActivity, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }


    private void inflateInterstitialAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        baseActivity.setContentView(R.layout.interstitial_ad_layout_test);

        // Add the Ad view into the ad container.
//        nativeAdLayout = baseActivity.findViewById(R.id.native_ad_container);
//        LayoutInflater inflater = LayoutInflater.from(baseActivity);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        //adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
//        adView = (LinearLayout) inflater.inflate(R.layout.interstitial_ad_layout, nativeAdLayout, false);
        adView = baseActivity.findViewById(R.id.ad_unit);
//        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
//        LinearLayout adChoicesContainer = baseActivity.findViewById(R.id.ad_choices_container);
//        AdOptionsView adOptionsView = new AdOptionsView(baseActivity, nativeAd, null);
//        adChoicesContainer.removeAllViews();
//        adChoicesContainer.addView(adOptionsView, 0);

        Button buttonAdExit = baseActivity.findViewById(R.id.ad_exit);
        buttonAdExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ActivityAlarmClock.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(myIntent);
                baseActivity.finish();
            }
        });

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

}
