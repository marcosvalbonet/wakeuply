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


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.valbonet.wakeuplyapp.presentation.PlayUserWebActivity;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.presentation.alarms.PreviewVideoActivity;
import com.valbonet.wakeuplyapp.presentation.searcher.SearcherUserActivity;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class is used for editing the card alarm settings.  Settings are broken
 * into two pieces: alarm information and actual settings.  Every alarm will
 * have alarm information.  Alarms will only have alarm settings if the user
 * has overridden the default settings for a given alarm.  This dialog is used
 * to edit both the application default settings and individual alarm settings.
 * When editing the application default settings, no AlarmInfo object will
 * be present.  When editing an alarm which hasn't yet had specific settings
 * set, AlarmSettings will contain the default settings.  There is one required
 * EXTRA that must be supplied when starting this activity: EXTRAS_ALARM_ID,
 * which should contain a long representing the alarmId of the settings
 * being edited.  AlarmSettings.DEFAULT_SETTINGS_ID can be used to edit the
 * default settings.
 */
public final class AlarmCardSettings implements View.OnClickListener,
        ToggleButton.OnCheckedChangeListener,
        TimePickerDialog.OnTimeChangedListener,
        TimePickerDialog.OnTimeSetListener {

    public static final String EXTRAS_ALARM_ID = "alarm_id";
    private static final int MISSING_EXTRAS = -69;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0;
    private static final String SETTINGS_VIBRATE_KEY = "SETTINGS_VIBRATE_KEY";
    private static final String SETTINGS_SNOOZE_KEY = "SETTINGS_SNOOZE_KEY";
    private static final String SETTINGS_TIME_HOUR_OF_DAY_KEY = "SETTINGS_TIME_HOUR_OF_DAY_KEY";
    private static final String SETTINGS_TIME_MINUTE_KEY = "SETTINGS_TIME_MINUTE_KEY";
    private static final String SETTINGS_TIME_SECOND_KEY = "SETTINGS_TIME_SECOND_KEY";
    private static final String SETTINGS_NAME_KEY = "SETTINGS_NAME_KEY";
    private static final String SETTINGS_VOLUME_START_PERCENT_KEY = "SETTINGS_VOLUME_START_PERCENT_KEY";
    private static final String SETTINGS_VOLUME_END_PERCENT_KEY = "SETTINGS_VOLUME_END_PERCENT_KEY";
    private static final String SETTINGS_VOLUME_CHANGE_TIME_SEC_KEY = "SETTINGS_VOLUME_CHANGE_TIME_SEC_KEY";
    private static final String SETTINGS_TONE_NAME_KEY = "SETTINGS_TONE_NAME_KEY";
    private static final String SETTINGS_TONE_URI_KEY = "SETTINGS_TONE_URI_KEY";
    private static final String SETTINGS_DAYS_OF_WEEK_KEY = "SETTINGS_DAYS_OF_WEEK_KEY";
    private static final String SETTINGS_VOLUME_PERCENT_KEY = "SETTINGS_VOLUME_PERCENT_KEY";
    private static final String SETTINGS_ID_USER_KEY = "SETTINGS_ID_USER_KEY";



    private enum SettingType {
        TIME,
        NAME,
        DAYS_OF_WEEK,
        TONE, SNOOZE,
        VIBRATE,
        VOLUME_FADE,
        VOLUME
    }

    public static final int NAME_PICKER = 1;
    public static final int DOW_PICKER = 2;
    public static final int TONE_PICKER = 3;
    public static final int SNOOZE_PICKER = 4;
    public static final int VOLUME_FADE_PICKER = 5;
    public static final int DELETE_CONFIRM = 6;
    public static final int EXPLAIN_READ_EXTERNAL_STORAGE = 7;
    public static final int PERMISSION_NOT_GRANTED = 8;
    public static final int VOLUME_PICKER = 9;
    public static final int TIME_PICKER = 10;
    public static final int VIDEO_PICKER = 11;

    private TimePickerDialog picker;
    static AlarmCardSettings.SettingsAdapter settingsAdapter;
    private Context context;
    private FragmentManager fragmentManager;
    private View view;

    private DbAccessor db;
    private AlarmInfo originalInfo;
    private static AlarmInfo info;
    private static long alarmId;
    private static AlarmClockServiceBinder service;
    private static AlarmSettings originalSettings;
    private static AlarmSettings settings;

    public AlarmCardSettings(Context context, FragmentManager fragmentManager, View view, long alarmId, AlarmClockServiceBinder service){
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.view = view;
        this.alarmId = alarmId;

        // Access to in-memory and persistent data structures.
        this.service = service;
        db = new DbAccessor(context);

        // Read the current settings from the database.  Keep a copy of the
        // original values so that we can write new values only if they differ
        // from the originals.
        originalInfo = db.readAlarmInfo(alarmId);
        // Info will not be available for the default settings.
        if (originalInfo != null) {
            info = new AlarmInfo(originalInfo);


        }
        originalSettings = db.readAlarmSettings(alarmId);
        settings = new AlarmSettings(originalSettings);

        settingsAdapter = new AlarmCardSettings.SettingsAdapter(context);

    }

    public void redrawFields(){


        //Init the repeating week days
        Calendar calendar = Calendar.getInstance();
        Week daysOfWeek =  info.getTime().getDaysOfWeek();
        for (int i = 0; i < Week.Day.values().length; ++i) {
            Week.Day alarmDay = Week.Day.values()[i];
//            Week.Day alarmDay = Week.calendarToDay(calendar.get(Calendar.DAY_OF_WEEK));
            if (daysOfWeek.hasDay(alarmDay)) {
                ((ToggleButton)view.findViewWithTag(alarmDay.name())).setChecked(true);
                ((ToggleButton)view.findViewWithTag(alarmDay.name())).setBackgroundResource(R.drawable.day_toggle_cheched);
            }else{
                ((ToggleButton)view.findViewWithTag(alarmDay.name())).setChecked(false);
                ((ToggleButton)view.findViewWithTag(alarmDay.name())).setBackgroundResource(R.drawable.day_toggle_uncheched);
            }
        }

        //findViewById(R.id.alarm_label)
        TextView labelName = (TextView) view.findViewById(R.id.label_name);
        TextView alarmLabel = (TextView) view.findViewById(R.id.alarm_label);

        labelName.setText(info.getName());
        alarmLabel.setText(info.getName());

        TextView tiktokVideo = (TextView) view.findViewById(R.id.tiktokVideo);
        TextView muserVideo = (TextView) view.findViewById(R.id.muserVideo);

        if (settings.getNickMuser() != null && !settings.getNickMuser().isEmpty()){
            tiktokVideo.setText(settings.getNickMuser());
            muserVideo.setText(settings.getNickMuser());
        }

        CheckBox random = (CheckBox) view.findViewById(R.id.random);
        random.setChecked(settings.getRandom());

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.alarm_time:
                final AlarmTime time = info.getTime();

                Calendar c = time.calendar();

                picker = TimePickerDialog.newInstance(
                        this,
                        this,
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(context)
                );

                if (AppSettings.isThemeDark(context)) {
                    picker.setThemeDark(true);
                }

                picker.setAccentColor(AppSettings.getTimePickerColor(
                        context));

                picker.vibrate(true);

                if (AppSettings.isDebugMode(context)) {
                    picker.enableSeconds(true);
                } else {
                    picker.enableSeconds(false);
                }

                picker.setTitle(time.timeUntilString(context));

                picker.show(fragmentManager, "TimePickerDialog");
                break;
            case R.id.label_name:
                showDialogFragment(NAME_PICKER);
                break;

            case R.id.snooze:
                showDialogFragment(SNOOZE_PICKER);
                break;

            case R.id.mr_volume_slider:
                showDialogFragment(VOLUME_FADE_PICKER);
                break;

            case R.id.volume:
                showDialogFragment(VOLUME_PICKER);
                break;
            case R.id.random:
                CheckBox randomSwitch = (CheckBox) v;
                settings.setRandom(randomSwitch.isChecked());
                settingsAdapter.notifyDataSetChanged();
                break;

            case R.id.tiktokVideo:

                    //Show A fragmentDialog with a "Search in Tiktok"
                //When the user click, go to SearcherUserActivity
                //And when close the PlayVideoActivity, we go back to the dialog with
                //the tiktok video choosed.
                if (settings.getNickMuser() == null) {
                    Intent myIntent = new Intent(context, SearcherUserActivity.class);
                    myIntent.putExtra("alarmID", alarmId);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(myIntent);
                }
                else  {
                    showDialogFragment(VIDEO_PICKER);
                }
                break;


        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String day = buttonView.getTag().toString();
        ToggleButton tb = (ToggleButton) buttonView;

        if (isChecked) {
            info.getTime().getDaysOfWeek().addDay(Week.Day.getDayByString(day));
            tb.setBackgroundResource(R.drawable.day_toggle_cheched);
        } else {
            info.getTime().getDaysOfWeek().removeDay(Week.Day.getDayByString(day));
            tb.setBackgroundResource(R.drawable.day_toggle_uncheched);
        }
        settingsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        final AlarmTime time = info.getTime();
        info.setTime(new AlarmTime(hourOfDay, minute, second, time.getDaysOfWeek()));
        settingsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTimeChanged(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        final AlarmTime infoTime = info.getTime();

        final AlarmTime time = new AlarmTime(hourOfDay, minute, second,
                infoTime.getDaysOfWeek());

        picker.setTitle(time.timeUntilString(context));
    }

    private void showDialogFragment(int id) {
        DialogFragment dialog = new AlarmCardSettings.ActivityDialogFragment().newInstance(id);
        dialog.show(fragmentManager, "ActivityDialogFragment");
    }



    public void notifyChanges(){
        settingsAdapter.notifyDataSetChanged();
    }

    public static class ActivityDialogFragment extends DialogFragment {

        public AlarmCardSettings.ActivityDialogFragment newInstance(int id) {
            AlarmCardSettings.ActivityDialogFragment fragment = new AlarmCardSettings.ActivityDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            switch (getArguments().getInt("id")) {

                case NAME_PICKER:
                    final View nameView = View.inflate(getActivity(),
                            R.layout.name_settings_dialog, null);
                    final TextView label = (TextView) nameView.findViewById(R.id.name_label);
                    label.setText(info.getName());
                    final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.alarm_label);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            info.setName(label.getEditableText().toString());
                            settingsAdapter.notifyDataSetChanged();
                            dismiss();
                        }
                    });
                    nameBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return nameBuilder.create();

                case DOW_PICKER:
                    final AlertDialog.Builder dowBuilder = new AlertDialog.Builder(getActivity());
                    dowBuilder.setTitle(R.string.scheduled_days);
                    dowBuilder.setMultiChoiceItems(
                            info.getTime().getDaysOfWeek().names(getActivity()),
                            info.getTime().getDaysOfWeek().bitmask(),
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    if (isChecked) {
                                        info.getTime().getDaysOfWeek().addDay(Week.Day.values()[which]);
                                    } else {
                                        info.getTime().getDaysOfWeek().removeDay(Week.Day.values()[which]);
                                    }
                                    settingsAdapter.notifyDataSetChanged();
                                }
                            });
                    dowBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return dowBuilder.create();

                case TONE_PICKER:

                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                    alert.setTitle("Tone");

                    return alert.create();

                case SNOOZE_PICKER:
                    // This currently imposes snooze times between 1 and 60 minutes,
                    // which isn't really necessary, but I think the picker is easier
                    // to use than a free-text field that you have to type numbers into.
                    final CharSequence[] items = new CharSequence[60];
                    // Note the array index is one-off from the value (the value of 1 is
                    // at index 0).
                    for (int i = 1; i <= 60; ++i) {
                        items[i-1] = Integer.toString(i);
                    }
                    final AlertDialog.Builder snoozeBuilder = new AlertDialog.Builder(getActivity());
                    snoozeBuilder.setTitle(R.string.snooze_minutes);
                    snoozeBuilder.setSingleChoiceItems(items, settings.getSnoozeMinutes() - 1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    settings.setSnoozeMinutes(item + 1);
                                    settingsAdapter.notifyDataSetChanged();
                                    dismiss();
                                }
                            });
                    return snoozeBuilder.create();

                case VOLUME_PICKER:

                    final AlertDialog.Builder volumeDialog = new AlertDialog.Builder(getActivity());

                    volumeDialog.setTitle("Volume");

                    return volumeDialog.create();

                case DELETE_CONFIRM:
                    final AlertDialog.Builder deleteConfirmBuilder = new AlertDialog.Builder(getActivity());
                    deleteConfirmBuilder.setTitle(R.string.delete);
                    deleteConfirmBuilder.setMessage(R.string.confirm_delete);
                    deleteConfirmBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            service.deleteAlarm(alarmId);
                            dismiss();
                            getActivity().finish();
                        }
                    });
                    deleteConfirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return deleteConfirmBuilder.create();
                case EXPLAIN_READ_EXTERNAL_STORAGE:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());

                    builder.setTitle(R.string.read_external_storage_title);

                    builder.setMessage(R.string.read_external_storage_message);

                    builder.setPositiveButton(R.string.grant,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();

//                                    _requestReadExternalStoragePermission(getActivity());
                                }
                            });

                    builder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            });

                    return builder.create();
                case PERMISSION_NOT_GRANTED:
                    final AlertDialog.Builder permissionBuilder = new AlertDialog.Builder(
                            getActivity());

                    permissionBuilder.setTitle(R.string.permission_not_granted_title);

                    permissionBuilder.setMessage(R.string.permission_not_granted);

                    permissionBuilder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            });

                    return permissionBuilder.create();

                case VIDEO_PICKER:
                    ArrayList<String> optionsList = new ArrayList();
                    optionsList.add(getResources().getString(R.string.search));
                    optionsList.add(getResources().getString(R.string.play_video));
                    if (settings.getUrlMuser() != null && !"".equals(settings.getUrlMuser())){
                        optionsList.add(getResources().getString(R.string.view)+ " " +settings.getNickMuser());
                    }
                    String[] options = new String[optionsList.size()];
                    optionsList.toArray(options);


                    AlertDialog.Builder videoChooserBuilder = new AlertDialog.Builder(getActivity());
                    //videoChooserBuilder.setTitle("Pick a color");
                    videoChooserBuilder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {;
                            // the user clicked on colors[which]
                            if (which == 0){
                                Intent myIntent = new Intent(getActivity().getApplicationContext(), SearcherUserActivity.class);
                                myIntent.putExtra("alarmID", alarmId);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().getApplicationContext().startActivity(myIntent);

                            }else if (which == 1){
                                Intent myIntent = new Intent(getActivity().getApplicationContext(), PreviewVideoActivity.class);
                                myIntent.putExtra("alarmID", alarmId);
                                myIntent.putExtra("videoURL", settings.getVideoAlarmUrl());
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().getApplicationContext().startActivity(myIntent);

                            }else if (which == 2){
                                Intent myIntent = new Intent(getActivity().getApplicationContext(), PlayUserWebActivity.class);
                                myIntent.putExtra("alarmID", alarmId);
                                myIntent.putExtra("urlUser", settings.getUrlMuser());
                                myIntent.putExtra("nickUser", settings.getNickMuser());
                                myIntent.putExtra("nameUser", settings.getNickMuser());
                                myIntent.putExtra("userImgURL", settings.getUrlImageMuser());
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().getApplicationContext().startActivity(myIntent);

                            }else{
                                Intent myIntent = new Intent(getActivity().getApplicationContext(), SearcherUserActivity.class);
                                myIntent.putExtra("alarmID", alarmId);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().getApplicationContext().startActivity(myIntent);
                            }
                        }
                    });
                    return videoChooserBuilder.create();

                default:
                    return super.onCreateDialog(savedInstanceState);
            }
        }

    }

    /**
     * This adapter populates the settings_items view with the data encapsulated
     * in the individual Setting objects.
     */
    private final class SettingsAdapter {
        private Context context;

        public SettingsAdapter(Context context) {
            this.context = context;

        }

        public void notifyDataSetChanged() {
            //write in the field the new value

            saveAlarmSettings();

            updateFields();
        }

        private void updateFields(){

            Activity contextActivity = (Activity) context;

            TextView labelName = (TextView) view.findViewById(R.id.label_name);
            TextView alarmLabel = (TextView) view.findViewById(R.id.alarm_label);

            labelName.setText(info.getName());
            alarmLabel.setText(info.getName());

            TextView alarmTime = (TextView) view.findViewById(R.id.alarm_time);
            final AlarmTime infoTime = info.getTime();
            alarmTime.setText(infoTime.localizedString(contextActivity));

            TextView repeatView = (TextView) view.findViewById(R.id.alarm_repeat);
            repeatView.setText(info.getTime().getDaysOfWeek().toString(context));

        }

        private void saveAlarmSettings() {
            // Write AlarmInfo if it changed.
            if (originalInfo != null && !originalInfo.equals(info)) {
                db.writeAlarmInfo(alarmId, info);

                // Explicitly enable the alarm if the user changed the time.
                // This will reschedule the alarm if it was already enabled.
                // It's also probably the right thing to do if the alarm wasn't
                // enabled.
                if (!originalInfo.getTime().equals(info.getTime())) {
                    service.scheduleAlarm(alarmId);
                } else if (originalInfo.enabled()
                        && !originalInfo.getName().equals(info.getName())) {
                    service.scheduleAlarm(alarmId);
                }
            }

            // Write AlarmSettings if they have changed.
            if (!originalSettings.equals(settings)) {
                db.writeAlarmSettings(alarmId, settings);
            }
        }

    }

}
