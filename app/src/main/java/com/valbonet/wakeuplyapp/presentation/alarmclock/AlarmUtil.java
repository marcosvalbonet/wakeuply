/****************************************************************************
 * Copyright 2010 kraigs.android@gmail.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ****************************************************************************/

package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.net.Uri;
import android.provider.Settings;

import java.lang.reflect.Field;

public final class AlarmUtil {
  static public Uri alarmIdToUri(long alarmId) {
    return Uri.parse("alarm_id:" + alarmId);
  }

  public static long alarmUriToId(Uri uri) {
    return Long.parseLong(uri.getSchemeSpecificPart());
  }

  enum Interval {
    SECOND(1000), MINUTE(60 * 1000), HOUR(60 * 60 * 1000);
    private long millis;
    public long millis() { return millis; }
    Interval(long millis) {
      this.millis = millis;
    }
  }

  public static long millisTillNextInterval(Interval interval) {
    long now = System.currentTimeMillis();
    return interval.millis() - now % interval.millis();
  }

  public static long nextIntervalInUTC(Interval interval) {
    long now = System.currentTimeMillis();
    return now + interval.millis() - now % interval.millis();
  }

  public static Uri getDefaultAlarmUri() {
    // DEFAULT_ALARM_ALERT_URI is only available after SDK version 5.
    // Fall back to the default notification if the default alarm is
    // unavailable.
    try {
      Field f = Settings.System.class.getField("DEFAULT_ALARM_ALERT_URI");
      return (Uri) f.get(null);
    } catch (Exception e) {
      return Settings.System.DEFAULT_NOTIFICATION_URI;
    }
  }

  public static String getTimeUntilNextAlarmMessage(long timeInMillis){
    long timeDifference = timeInMillis - System.currentTimeMillis();
    long days = timeDifference / (1000 * 60 * 60 * 24);
    long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
    long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
    long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
    String alert = "La alarma sonará en ";
    if (days > 0) {
      alert += String.format(
              "%d días, %d horas, %d minutos y %d segundos", days,
              hours, minutes, seconds);
    } else {
      if (hours > 0) {
        alert += String.format("%d horas, %d minutos y %d segundos",
                hours, minutes, seconds);
      } else {
        if (minutes > 0) {
          alert += String.format("%d minutos, %d segundos", minutes,
                  seconds);
        } else {
          alert += String.format("%d segundos", seconds);
        }
      }
    }
    return alert;
  }
}
