package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReceiverNotificationRefresh extends BroadcastReceiver {

  public static void startRefreshing(Context context) {
    context.sendBroadcast(intent(context));
  }

  public static void stopRefreshing(Context context) {
    final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    manager.cancel(pendingIntent(context));
  }

  private static Intent intent(Context context) {
    return new Intent(context, ReceiverNotificationRefresh.class);
  }

  private static PendingIntent pendingIntent(Context context) {
    return PendingIntent.getBroadcast(context, 0, intent(context), 0);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    final Intent causeRefresh = new Intent(context, AlarmClockService.class);
    causeRefresh.putExtra(AlarmClockService.COMMAND_EXTRA, AlarmClockService.COMMAND_NOTIFICATION_REFRESH);

      context.startService(causeRefresh);

    long next = AlarmUtil.nextIntervalInUTC(AlarmUtil.Interval.MINUTE);
    final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        manager.set(AlarmManager.RTC_WAKEUP, next, pendingIntent(context));
    } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        manager.setExact(AlarmManager.RTC_WAKEUP, next, pendingIntent(context));
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pendingIntent(context));
    } else {
        manager.setExact(AlarmManager.RTC_WAKEUP, next, pendingIntent(context));
    }
  }

}
