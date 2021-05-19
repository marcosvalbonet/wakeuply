package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra("com.my.app.notificationId", 0);
        /* Your code to handle the event here */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel( notificationId ) ;
        MusicControl.getInstance(context).stopMusic();
    }
}
