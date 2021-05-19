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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DbHelper extends SQLiteOpenHelper {
  public static final String DB_NAME = "wakeuply";
  public static final int DB_VERSION = 3;

  public static final String DB_TABLE_ALARMS = "alarms";
  public static final String ALARMS_COL__ID = "_id";
  public static final String ALARMS_COL_TIME = "time";
  public static final String ALARMS_COL_ENABLED = "enabled";
  public static final String ALARMS_COL_NAME = "name";
  public static final String ALARMS_COL_DAY_OF_WEEK = "dow";

  public static final String DB_TABLE_SETTINGS = "settings";
  public static final String SETTINGS_COL_ID = "id";
  public static final String SETTINGS_COL_TONE_URL = "tone_url";
  public static final String SETTINGS_COL_TONE_NAME = "tone_name";
  public static final String SETTINGS_COL_SNOOZE = "snooze";
  public static final String SETTINGS_COL_VIBRATE = "vibrate";
  public static final String SETTINGS_COL_VOLUME_STARTING = "vol_start";
  public static final String SETTINGS_COL_VOLUME_ENDING = "vol_end";
  public static final String SETTINGS_COL_VOLUME_TIME = "vol_time";
  public static final String SETTINGS_COL_VOLUME = "volume";
  public static final String SETTINGS_COL_ID_USER = "id_user";
  public static final String SETTINGS_COL_NICK_MUSER = "nick_muser";
  public static final String SETTINGS_COL_URL_MUSER = "url_muser";
  public static final String SETTINGS_COL_VIDEO_ALARM_NAME = "video_alarm_name";
  public static final String SETTINGS_COL_URL_VIDEO_ALARM = "url_video_alarm";
  public static final String SETTINGS_COL_URL_IMAGE_MUSER = "url_image_muser";
  public static final String SETTINGS_COL_RANDOM = "random";


  public DbHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Alarm metadata table:
    // |(auto primary) | (0 to 86399) | (boolean) | (string) | (bitmask(7)) |
    // |     _id       |    time      |  enabled  |   name   |     dow      |
    // time is seconds past midnight.
    db.execSQL("CREATE TABLE " + DB_TABLE_ALARMS + " (" 
        + ALARMS_COL__ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + ALARMS_COL_NAME + " TEXT, "
        + ALARMS_COL_DAY_OF_WEEK + " UNSIGNED INTEGER (0, 127), "
        + ALARMS_COL_TIME + " UNSIGNED INTEGER (0, 86399),"
        + ALARMS_COL_ENABLED + " UNSIGNED INTEGER (0, 1))");
    // |(primary) | (string) | (string)  | (1 to 60) | (boolean) | (0 to 100) | (0 to 100) | (0 to 60) |
    // |   id     | tone_url | tone_name |   snooze  |  vibrate  |  vol_start |  vol_end   | vol_time  |
    // snooze is in minutes.
    db.execSQL("CREATE TABLE " + DB_TABLE_SETTINGS + " (" 
        + SETTINGS_COL_ID + " INTEGER PRIMARY KEY, "
        + SETTINGS_COL_TONE_URL + " TEXT,"
        + SETTINGS_COL_TONE_NAME + " TEXT,"
        + SETTINGS_COL_SNOOZE + " UNSIGNED INTEGER (1, 60),"
        + SETTINGS_COL_VIBRATE + " UNSIGNED INTEGER (0, 1),"
        + SETTINGS_COL_VOLUME_STARTING + " UNSIGNED INTEGER (1, 100),"
        + SETTINGS_COL_VOLUME_ENDING + " UNSIGNED INTEGER (1, 100),"
        + SETTINGS_COL_VOLUME_TIME + " UNSIGNED INTEGER (1, 60),"
        + SETTINGS_COL_VOLUME + " UNSIGNED INTEGER (1, 100),"
        + SETTINGS_COL_ID_USER + " TEXT,"
        + SETTINGS_COL_NICK_MUSER + " TEXT,"
        + SETTINGS_COL_URL_MUSER + " TEXT,"
        + SETTINGS_COL_VIDEO_ALARM_NAME + " TEXT,"
        + SETTINGS_COL_URL_VIDEO_ALARM + " TEXT,"
        + SETTINGS_COL_URL_IMAGE_MUSER + " TEXT,"
        + SETTINGS_COL_RANDOM + " UNSIGNED INTEGER (0, 1))");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    switch(oldVersion){
      case 1:
        db.execSQL("ALTER TABLE " + DB_TABLE_SETTINGS + " ADD COLUMN " + SETTINGS_COL_URL_IMAGE_MUSER + " TEXT;");
      case 2:
        db.execSQL("ALTER TABLE " + DB_TABLE_SETTINGS + " ADD COLUMN " + SETTINGS_COL_RANDOM + " UNSIGNED INTEGER (0, 1);");
    }
  }
}
