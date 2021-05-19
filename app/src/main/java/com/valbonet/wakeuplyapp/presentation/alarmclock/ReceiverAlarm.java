package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.presentation.alarms.AlarmAlertVideoActivity;


public class ReceiverAlarm extends BroadcastReceiver {

  String TAG = "ReceiverAlarm";

    @Override
    public void onReceive(Context context, Intent recvIntent) {
        Uri alarmUri = recvIntent.getData();
        if (alarmUri== null) return;

        long alarmId = AlarmUtil.alarmUriToId(alarmUri);

        try {
          WakeLock.acquire(context, alarmId);
        } catch (WakeLock.WakeLockException e) {
          if (AppSettings.isDebugMode(context)) {
            throw new IllegalStateException(e.getMessage());
          }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          createAlarmNotification(context, alarmUri);

        }else {

          Intent notifyService = new Intent(context, NotificationService.class);
          //Intent notifyService = new Intent(context, AlarmAlertVideoActivity.class);
          notifyService.setData(alarmUri);
          context.startService(notifyService);
        }
    }

  private void createAlarmNotification(Context context, Uri alarmUri){

    /**
     * Notification channel containing all alarm notifications.
     */
    String ALARM_NOTIFICATION_CHANNEL_ID = "alarmNotification";
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    CharSequence name = "Default";///getString(R.string.channel_name);// The user-visible name of the channel.
    int importance = NotificationManager.IMPORTANCE_HIGH;

    int notificationId = 72;

    Notification.Builder builder = new Notification.Builder(context);
    builder.setSmallIcon(R.drawable.ic_stat_notify_alarm);
    builder.setContentTitle("Wakeup.ly");
    builder.setContentText(Utils.getHelloByHour(context));
    //builder.setWhen(0);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(ALARM_NOTIFICATION_CHANNEL_ID);
    }
    builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)


            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(Notification.VISIBILITY_PUBLIC) ;
    //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_stat_notify_alarm));

    Intent alarmIntent = new Intent(context, AlarmAlertVideoActivity.class);
    alarmIntent.setData(alarmUri);
    alarmIntent.putExtra("com.my.app.notificationId", notificationId);
    alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

    builder.setContentIntent(pendingIntent);
    builder.setAutoCancel(true);

    Intent fullscreenIntent = new Intent(context, AlarmAlertVideoActivity.class);
    fullscreenIntent.setData(alarmUri);
    fullscreenIntent.putExtra("com.my.app.notificationId", notificationId);
    fullscreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingFullScreenIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, PendingIntent.FLAG_ONE_SHOT);
    // Setup fullscreen intent
    builder.setFullScreenIntent(pendingFullScreenIntent, true);
    builder.setPriority(Notification.PRIORITY_MAX);

    Log.d(TAG, "setFullScreenIntent: ");


    Intent dismissAlarmIntent = new Intent(context, NotificationDismissedReceiver.class);
    dismissAlarmIntent.setData(alarmUri);
    dismissAlarmIntent.putExtra("com.my.app.notificationId", notificationId);
    dismissAlarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent dismissPendingIntent =  PendingIntent.getBroadcast(context, 0, dismissAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.addAction(R.drawable.ic_dialog_close_dark, "Dismiss", dismissPendingIntent);

    //        AudioAttributes audioAttributes = new AudioAttributes.Builder()
    //                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
    //                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
    //                .build();

    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel mChannel = new NotificationChannel(ALARM_NOTIFICATION_CHANNEL_ID, name, importance);
      mChannel.enableVibration(true);

      //Sets whether notifications from these Channel should be visible on Lockscreen or not
      mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

      //mChannel.setSound(path, audioAttributes);
      // Create a notification and set the notification channel.
      manager.createNotificationChannel(mChannel);
    }
    Log.d(TAG, "notify: ");
    manager.notify(notificationId, builder.build());

    MusicControl musicControl = MusicControl.getInstance(context.getApplicationContext());
    MediaPlayer mediaPlayer = musicControl.getMediaPlayer();

    AlarmSettings settings;
    DbAccessor db = new DbAccessor(context);
    if (alarmUri != null) {
      long alarmId = AlarmUtil.alarmUriToId(alarmUri);
      settings = db.readAlarmSettings(alarmId);
    }else{
      settings = new AlarmSettings();
    }

    /*try {
      AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
      audioManager.setStreamVolume(AudioManager.STREAM_ALARM, settings.getVolumePercent(), 0);
      //                mediaPlayer.setDataSource(context.getApplicationContext(), alert);
      mediaPlayer.setVolume(Utils.getFloatVolume(settings.getVolumePercent()), Utils.getFloatVolume(settings.getVolumePercent()));
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
      //                mediaPlayer.setLooping(true);
      //                mediaPlayer.prepare();
      musicControl.stopMusic();
//        musicControl.playMusic(context.getApplicationContext(), settings.getTone());

    } catch (Exception e) {
      mediaPlayer.stop();
    }*/
  }
}
