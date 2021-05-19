package com.valbonet.wakeuplyapp.presentation.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.presentation.ads.mediators.AlarmFacebookNative;

public class FacebookNativeActivity extends Activity {

    AlarmFacebookNative alarmFacebookNative;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.facebook_native_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        alarmFacebookNative = new AlarmFacebookNative(this);
        alarmFacebookNative.init();
    }
}
