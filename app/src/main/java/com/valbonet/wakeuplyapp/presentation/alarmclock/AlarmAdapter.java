package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.RemoteException;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.valbonet.wakeuplyapp.data.LoadImageTask;
import com.valbonet.wakeuplyapp.R;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ContentViewHolder> {

    private ArrayList<AlarmInfo> alarmInfos;
    private AlarmClockServiceBinder service;
    private Context context;
    private Animation animationUp;
    private Animation animationDown;

    private CardView alarmSettings;
    private int actualPosition = 0;

    public AlarmAdapter(ArrayList<AlarmInfo> alarmInfos,
            AlarmClockServiceBinder service, Context context) {
        this.alarmInfos = alarmInfos;
        this.service = service;
        this.context = context;

        this.animationUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        this.animationDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);

    }

    public ArrayList<AlarmInfo> getAlarmInfos() {
        return alarmInfos;
    }

    public void removeAt(int position) {
        alarmInfos.remove(position);

        notifyItemRemoved(position);

        notifyItemRangeChanged(position, alarmInfos.size());
    }

    public void removeAll() {
        int size = alarmInfos.size();

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                alarmInfos.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, final int position) {
        final AlarmInfo info = alarmInfos.get(position);

        AlarmTime time = null;
        // See if there is an instance of this alarm scheduled.
        if (service.clock() != null) {
            try {
                time = service.clock().pendingAlarm(info.getAlarmId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        // If we couldn't find a pending alarm, display the configured time.
        if (time == null) {
            time = info.getTime();
        }

        String timeStr = time.localizedString(context);
        String alarmId = "";
        if (AppSettings.isDebugMode(context)) {
            alarmId = " [" + info.getAlarmId() + "]";
        }
        String timeText = timeStr + alarmId;

        holder.timeView.setText(timeText);

        holder.nextView.setText(time.timeUntilString(context));

        holder.labelView.setText(info.getName());

        holder.muserVideo.setText(info.getMuserVideo());

        if(info.getMuserUrlImg() != null) {
            LoadImageTask loadImage = new LoadImageTask(holder.tiktokerImage, info.getMuserVideo());
            loadImage.execute(info.getMuserUrlImg());
        }

        if (!info.getTime().getDaysOfWeek().equals(Week.NO_REPEATS)) {
            holder.repeatView.setText(info.getTime().getDaysOfWeek().
                    toString(context));
        }

        holder.enabledView.setChecked(info.enabled());

        holder.enabledView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final AlarmInfo info = alarmInfos.get(position);

                if (isChecked) {
                    info.setEnabled(true);

                    service.scheduleAlarm(info.getAlarmId());
                } else {
                    info.setEnabled(false);

                    service.unscheduleAlarm(info.getAlarmId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return alarmInfos.size();
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);

        return new ContentViewHolder(itemView);
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        protected ImageView tiktokerImage;
        protected TextView timeView;
        protected TextView nextView;
        protected TextView labelView;
        protected TextView muserVideo;
        protected TextView repeatView;
        protected SwitchCompat enabledView;

        protected TextView labelName;
        protected CheckBox vibrate;
        protected TextView tiktokAlarm;
        protected ImageView hideSettings;
        protected CheckBox random;

        protected ToggleButton monday;
        protected ToggleButton tuesday;
        protected ToggleButton wednesday;
        protected ToggleButton thursday;
        protected ToggleButton friday;
        protected ToggleButton saturday;
        protected ToggleButton sunday;



        public ContentViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            tiktokerImage = (ImageView) view.findViewById(R.id.muserImage);
            timeView = (TextView) view.findViewById(R.id.alarm_time);
            nextView = (TextView) view.findViewById(R.id.next_alarm);
            labelView = (TextView) view.findViewById(R.id.alarm_label);
            muserVideo = (TextView) view.findViewById(R.id.muserVideo);
            repeatView = (TextView) view.findViewById(R.id.alarm_repeat);
            enabledView = (SwitchCompat) view.findViewById(R.id.alarm_enabled);

            labelName = (TextView) view.findViewById(R.id.label_name);
            vibrate = (CheckBox) view.findViewById(R.id.vibration);
            tiktokAlarm = (TextView) view.findViewById(R.id.tiktokVideo);
            hideSettings = (ImageView) view.findViewById(R.id.hide);
            random = (CheckBox) view.findViewById(R.id.random);

            monday = view.findViewById(R.id.monday);
            tuesday = view.findViewById(R.id.tuesday);
            wednesday = view.findViewById(R.id.wednesday);
            thursday = view.findViewById(R.id.thursday);
            friday = view.findViewById(R.id.friday);
            saturday = view.findViewById(R.id.saturday);
            sunday = view.findViewById(R.id.sunday);
        }

        public void openAlarmSettings(Context context) {
            final AlarmInfo info = alarmInfos.get(getAdapterPosition());

            final Intent i = new Intent(context, ActivityAlarmSettings.class);

            i.putExtra(ActivityAlarmSettings.EXTRAS_ALARM_ID, info.getAlarmId());

            context.startActivity(i);
        }

        @Override
        public void onClick(View v) {
            if (actualPosition != getAdapterPosition()){
                if (alarmSettings != null && alarmSettings.isShown()){
                    alarmSettings.setVisibility(View.GONE);
                    alarmSettings.startAnimation(animationUp);
                }
            }

            alarmSettings = (CardView) v.findViewById(R.id.alarm_settings);

            if(!alarmSettings.isShown()){
                fieldsOnClickListener(v);

                alarmSettings.setVisibility(View.VISIBLE);
                alarmSettings.startAnimation(animationDown);

                actualPosition = getAdapterPosition();

            }

        }

        public void fieldsOnClickListener(View v){
            final AlarmInfo info = alarmInfos.get(getAdapterPosition());
            Activity host = (Activity) v.getContext();
            AlarmCardSettings alarmSettingsListener = new AlarmCardSettings(v.getContext(), host.getFragmentManager(), v, info.getAlarmId(), service);

            //Put info in the fields to edit
            alarmSettingsListener.redrawFields();

            //add all fields that we need to change
            tiktokerImage.setOnClickListener(alarmSettingsListener);
            timeView.setOnClickListener(alarmSettingsListener);
            labelName.setOnClickListener(alarmSettingsListener);
            vibrate.setOnClickListener(alarmSettingsListener);
            tiktokAlarm.setOnClickListener(alarmSettingsListener);
            random.setOnClickListener(alarmSettingsListener);

            monday.setOnCheckedChangeListener(alarmSettingsListener);
            tuesday.setOnCheckedChangeListener(alarmSettingsListener);
            wednesday.setOnCheckedChangeListener(alarmSettingsListener);
            thursday.setOnCheckedChangeListener(alarmSettingsListener);
            friday.setOnCheckedChangeListener(alarmSettingsListener);
            saturday.setOnCheckedChangeListener(alarmSettingsListener);
            sunday.setOnCheckedChangeListener(alarmSettingsListener);


            hideSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarmSettings.setVisibility(View.GONE);
                    alarmSettings.startAnimation( AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_up));
                }
            });
        }

        @Override
        public boolean onLongClick(View v) {
            final CharSequence actions[] = new CharSequence[] {
                    context.getString(R.string.delete)
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(actions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (actions[which].equals(actions[0])) {
                        final AlarmInfo info = alarmInfos.get(getAdapterPosition());

                        DialogFragment delete = new ActivityAlarmClock.ActivityDialogFragment().newInstance(
                                ActivityAlarmClock.DELETE_ALARM_CONFIRM, info,
                                getAdapterPosition());

                        delete.show(((Activity) context).getFragmentManager(),
                                "ActivityDialogFragment");
                    }
                }
            });
            builder.show();

            return true;
        }
    }

}
