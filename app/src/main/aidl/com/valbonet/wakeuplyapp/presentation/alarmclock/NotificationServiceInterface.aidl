package com.valbonet.wakeuplyapp.presentation.alarmclock;

interface NotificationServiceInterface {
  long currentAlarmId();
  int firingAlarmCount();
  float volume();
  void acknowledgeCurrentNotification(int snoozeMinutes);
}