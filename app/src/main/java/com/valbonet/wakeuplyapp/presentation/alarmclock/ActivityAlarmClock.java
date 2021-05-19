package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.data.DataRepository;
import com.valbonet.wakeuplyapp.presentation.PlayVideoPagerAdapterActivity;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.presentation.alarms.PreviewVideoActivity;
import com.valbonet.wakeuplyapp.presentation.navigationmenu.WatchFragment;
import com.valbonet.wakeuplyapp.utils.RateThisApp;
import com.valbonet.wakeuplyapp.utils.SpecialConfigurationHelper;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.data.connection.Config;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.presentation.navigationmenu.ItemMainFragment;
import com.valbonet.wakeuplyapp.presentation.navigationmenu.ItemPremiumFragment;
import com.valbonet.wakeuplyapp.presentation.navigationmenu.ItemShareFragment;
import com.valbonet.wakeuplyapp.presentation.navigationmenu.TiktokerSearcherFragment;
import com.wdullaer.materialdatetimepicker.time.*;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is the main Activity for the application.  It contains a ListView
 * for displaying all alarms, a simple clock, and a button for adding new
 * alarms.  The context menu allows the user to edit default settings.  Long-
 * clicking on the clock will trigger a dialog for enabling/disabling 'debug
 * mode.'
 */
public final class ActivityAlarmClock extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        TimePickerDialog.OnTimeChangedListener {

    public static final int DELETE_CONFIRM = 1;
    public static final int DELETE_ALARM_CONFIRM = 2;

    public static final int ACTION_TEST_ALARM = 0;
    public static final int ACTION_PENDING_ALARMS = 1;


    public static final String BOTTOM_MENU_HOME = "home";
    public static final String BOTTOM_MENU_SEARCH = "search";
    public static final String BOTTOM_MENU_ALARM = "newAlarm";
    public static final String BOTTOM_MENU_SHARE = "share";
    public static final String BOTTOM_MENU_PREMIUM = "premium";

    private TimePickerDialog picker;
    public static ActivityAlarmClock activityAlarmClock;

    private static AlarmClockServiceBinder service;
    private static NotificationServiceBinder notifyService;
    private DbAccessor db;
    private static AlarmAdapter adapter;
    private Cursor cursor;
    private Handler handler;
    private Runnable tickCallback;
    private static RecyclerView alarmList;
    private int mLastFirstVisiblePosition;

    private BottomNavigationView bottomNavigationView;

    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean isDeepLinkLoaded = false;

    // Logcat tag
    private static final String TAG = "ActivityAlarmClock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppSettings.setMainActivityTheme(getBaseContext(),
                ActivityAlarmClock.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        activityAlarmClock = this;

        // Access to in-memory and persistent data structures.
        service = new AlarmClockServiceBinder(getApplicationContext());

        // Init new Database
        db = new DbAccessor(getApplicationContext());
        handler = new Handler();


        // Setup the alarm list and the underlying adapter. Clicking an individual
        // item will start the settings activity.
        alarmList = (RecyclerView) findViewById(R.id.alarm_list);

        adapter = new AlarmAdapter(new ArrayList<AlarmInfo>(), service, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        alarmList.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final AlarmInfo alarmInfo = adapter.getAlarmInfos().
                        get(viewHolder.getAdapterPosition());

                final long alarmId = alarmInfo.getAlarmId();
                final AlarmSettings alarmSettings = db.readAlarmSettings(alarmId);

                removeItemFromList(ActivityAlarmClock.this, alarmId,
                        viewHolder.getAdapterPosition());

                Snackbar snack = Snackbar.make(findViewById(R.id.placeSnackBar),
                        getString(R.string.alarm_deleted), Snackbar.LENGTH_LONG);

                snack.setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        undoAlarmDeletion(alarmInfo.getTime(),
                                alarmSettings,
                                alarmInfo.getName(), alarmInfo.enabled());
                    }
                });


                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                        snack.getView().getLayoutParams();
                params.setMargins(0, 0, 0, 150);
                snack.getView().setLayoutParams(params);
                snack.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(alarmList);


        initializeBottomMenu();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);

        // This is a self-scheduling callback that is responsible for refreshing
        // the screen.  It is started in onResume() and stopped in onPause().
        tickCallback = new Runnable() {
            @Override
            public void run() {
                // Redraw the screen.
                redraw();

                // Schedule the next update on the next interval boundary.
                AlarmUtil.Interval interval = AlarmUtil.Interval.MINUTE;

                if (AppSettings.isDebugMode(getApplicationContext())) {
                    interval = AlarmUtil.Interval.SECOND;
                }

                long next = AlarmUtil.millisTillNextInterval(interval);

                handler.postDelayed(tickCallback, next);
            }
        };

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Intialize Wakeuply");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


    }

    @Override
    protected void onStart() {
        super.onStart();

        //FirebaseDynamicLinks
        checkFirebaseDynamicLinks();

        // Arreglado para Huawei. Verificado
//        AlertMessages alertMessages = AlertMessages.getInstance(this);
//        alertMessages.ifHuaweiAlert();

//        SpecialConfigurationHelper.getInstance().resetPreferences(this);
        SpecialConfigurationHelper.getInstance().getAutoStartPermission(this);
        SpecialConfigurationHelper.getInstance().getOtherPermission(this);


        // Puntua la app
        RateThisApp.init(new RateThisApp.Config(3, 10));
        RateThisApp.onCreate(this);
        RateThisApp.onStart(this);
        if (RateThisApp.shouldShowRateDialog()){
            RateThisApp.showRateDialog(this);
        }

        // TODO: see if exist a new version of app
        //Check For last Version
        if (Utils.existNewVersionApp(this)){
            Utils.showNewVersionDialog(this);
        }

        if(Utils.isConnectingToInternet(this)){
            initData();
        }

    }

    private void checkFirebaseDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        if (pendingDynamicLinkData != null) {
                            Uri deepTiktokLink = pendingDynamicLinkData.getLink();
                            Log.i(TAG, "Firebase DeepLink: " + deepTiktokLink);

                            // call to Activity to show this tiktok deepTiktokLink
                            if (!isDeepLinkLoaded){
                                Intent myIntent = new Intent(getApplicationContext(), PreviewVideoActivity.class);
                                isDeepLinkLoaded = true;
                                //myIntent.putExtra("alarmID", alarmId);
                                myIntent.putExtra(Constants.videoURL, deepTiktokLink.toString());
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(myIntent);
                            }
                        }
                    }
                }) .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<PendingDynamicLinkData>() {
                    @Override
                    public void onComplete(@NonNull Task<PendingDynamicLinkData> task) {

                    }
                });
    }

    private void initData() {
        //Initialize Data
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //TiktokerSearcherFragment.musersList = Data.getTiktokMusers(TiktokerSearcherFragment.listSize);

                if (Constants.TEST) {
                    WatchFragment.randomTiktokList = Data.getRandomTiktokMusers(2);
                }

                Config.getJSPlayUser();
                Config.getJSLaunchClickedTikTokVideo();
            }
        });
        thread.start();
    }

    private void initializeBottomMenu(){

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setItemIconTintList(null);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        //bottomNavigationView.setVisibility(View.INVISIBLE);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = ItemMainFragment.newInstance();
                                break;
                            case R.id.action_item2:
                                selectedFragment = TiktokerSearcherFragment.newInstance();
                                break;
                            case R.id.action_item3:
                                selectedFragment = ItemMainFragment.newInstance();
                                showPickerClock();
                                break;
                            case R.id.action_item4:
                                selectedFragment = ItemShareFragment.newInstance();
                                break;
                            case R.id.action_item5:
                                if (Constants.TEST){
//                                    String URL = "https://www.tiktok.com/@leaelui/video/6916913221857201409";
//                                    Data.getVideoFile(getApplicationContext(), URL);

                                    selectedFragment = WatchFragment.newInstance();

                                }else{
                                    selectedFragment = ItemPremiumFragment.newInstance();
                                }
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();


                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ItemMainFragment.newInstance());
        transaction.commit();


        /*Code for initialize bottom menu*/
        String bottomMenu = BOTTOM_MENU_HOME;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("bottomMenu")){
            bottomMenu = bundle.getString("bottomMenu");
        }

        switch (bottomMenu){
            case BOTTOM_MENU_HOME:
                bottomNavigationView.setSelectedItemId(R.id.action_item1);
                break;

            case BOTTOM_MENU_ALARM:
                bottomNavigationView.setSelectedItemId(R.id.action_item3);
                break;
        }
    }

    private void undoAlarmDeletion(AlarmTime alarmTime,
                                   AlarmSettings alarmSettings, String alarmName, boolean enabled) {
        long newAlarmId = service.resurrectAlarm(alarmTime, alarmName, enabled);

        if (newAlarmId != AlarmClockServiceBinder.NO_ALARM_ID) {
            db.writeAlarmSettings(newAlarmId, alarmSettings);

            requery();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        invalidateOptionsMenu();
        service.bind();
        handler.post(tickCallback);
        requery();

        alarmList.getLayoutManager().scrollToPosition(mLastFirstVisiblePosition);

        notifyService = new NotificationServiceBinder(getApplicationContext());
        notifyService.bind();
        notifyService.call(new NotificationServiceBinder.ServiceCallback() {
            @Override
            public void run(NotificationServiceInterface service) {
                int count;

                try {
                    count = service.firingAlarmCount();
                } catch (RemoteException e) {
                    return;
                }

                if (count > 0) {
                    Intent notifyActivity = new Intent(getApplicationContext(),
                            ActivityAlarmNotification.class);

                    notifyActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(notifyActivity);
                }
            }
        });

        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().
                findFragmentByTag("TimePickerDialog");

        if (tpd != null) {
            picker = tpd;

            tpd.setOnTimeSetListener(this);

            tpd.setOnTimeChangedListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacks(tickCallback);

        service.unbind();

        if (notifyService != null) {
            notifyService.unbind();
        }

        mLastFirstVisiblePosition = ((LinearLayoutManager)
                alarmList.getLayoutManager()).
                findFirstCompletelyVisibleItemPosition();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.closeConnections();

        activityAlarmClock = null;

        notifyService = null;

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AppSettings.isDebugMode(getApplicationContext())) {
            menu.add(Menu.NONE, ACTION_TEST_ALARM, 5, R.string.test_alarm);

            menu.add(Menu.NONE, ACTION_PENDING_ALARMS, 6, R.string.pending_alarms);
        }

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        AlarmTime time = new AlarmTime(hourOfDay, minute, second);

        service.createAlarm(time);

        requery();
    }

    @Override
    public void onTimeChanged(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        AlarmTime time = new AlarmTime(hourOfDay, minute, second);

        picker.setTitle(time.timeUntilString(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_new:
                showPickerClock();
                break;
            case R.id.action_app_settings:
                Intent app_settings = new Intent(getApplicationContext(),
                        ActivityAppSettings.class);

                startActivity(app_settings);
                break;
            case R.id.action_app_share:
                //TO TEST the new Insterstitial ads
                if (Constants.TEST){
                    Intent myIntent = new Intent(getApplicationContext(), PlayVideoPagerAdapterActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(myIntent);
//                    if (videoPage != null)
//                        Log.d("Test Random Videos", videoPage);
                }else{
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, ItemShareFragment.newInstance());
                    transaction.commit();
                }

                break;
            case R.id.action_app_premium:

                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                transaction2.replace(R.id.frame_layout, ItemPremiumFragment.newInstance());
                transaction2.commit();
                break;
            case ACTION_TEST_ALARM:
                // Used in debug mode.  Schedules an alarm for 5 seconds in the future
                // when clicked.
                final Calendar testTime = Calendar.getInstance();

                testTime.add(Calendar.SECOND, 5);

                AlarmTime time = new AlarmTime(
                        testTime.get(Calendar.HOUR_OF_DAY),
                        testTime.get(Calendar.MINUTE),
                        testTime.get(Calendar.SECOND));

                service.createAlarm(time);

                requery();
                break;
            case ACTION_PENDING_ALARMS:
                // Displays a list of pending alarms (only visible in debug mode).
               // startActivity(new Intent(getApplicationContext(),
                //        ActivityPendingAlarms.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialogFragment(int id) {
        DialogFragment dialog = new ActivityDialogFragment().newInstance(
                id);

        dialog.show(getFragmentManager(), "ActivityDialogFragment");
    }

    private void redraw() {
        // Recompute expiration times in the list view
        // Remove it we resolve problem with enabled and disabled alarms. Now just for first alarm
        if (alarmList.getChildCount()==0){
            adapter.notifyDataSetChanged();
            //adapter.refreshExpirationTimes(alarmList);
        }

        Calendar now = Calendar.getInstance();

        AlarmTime time = new AlarmTime(now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), 0);

    }

    private void requery() {
        cursor = db.readAlarmInfo();

        ArrayList<AlarmInfo> infos = new ArrayList<>();

        while (cursor.moveToNext()) {
            AlarmInfo alarmInfo = new AlarmInfo(cursor);
            Long alarmId = cursor.getLong(cursor.getColumnIndex(DbHelper.ALARMS_COL__ID));
            AlarmSettings originalSettings = db.readAlarmSettings(alarmId);
            alarmInfo.setMuserVideo(originalSettings.getNickMuser());
            alarmInfo.setMuserUrlImg(originalSettings.getUrlImageMuser());

            infos.add(alarmInfo);

        }

        adapter = new AlarmAdapter(infos, service, this);
        alarmList.setAdapter(adapter);

        setEmptyViewIfEmpty(this);

        //Resolve a bug when the image empty_view is gone and alarmList is Visible
        if (adapter.getItemCount() == 1) {
            handler.post(tickCallback);
        }

    }

    public static void setEmptyViewIfEmpty(Activity activity) {
        if (adapter.getItemCount() == 0) {
            activity.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            alarmList.setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.empty_view).setVisibility(View.GONE);
            alarmList.setVisibility(View.VISIBLE);
        }
    }

    public static void removeItemFromList(Activity activity, long alarmId, int position) {
        if (adapter.getItemCount() == 1) {
            ((AppBarLayout) activity.findViewById(R.id.app_bar)).
                    setExpanded(true);
        }

        service.deleteAlarm(alarmId);
        adapter.removeAt(position);
        setEmptyViewIfEmpty(activity);
    }

    public static class ActivityDialogFragment extends DialogFragment {

        public ActivityDialogFragment newInstance(int id) {
            ActivityDialogFragment fragment = new ActivityDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            fragment.setArguments(args);
            return fragment;
        }

        public ActivityDialogFragment newInstance(int id, AlarmInfo info,
                int position) {
            ActivityDialogFragment fragment = new ActivityDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            args.putLong("alarmId", info.getAlarmId());
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            switch (getArguments().getInt("id")) {
                case ActivityAlarmClock.DELETE_CONFIRM:
                    final AlertDialog.Builder deleteConfirmBuilder =
                            new AlertDialog.Builder(getActivity());

                    deleteConfirmBuilder.setTitle(R.string.delete_all);
                    deleteConfirmBuilder.setMessage(R.string.confirm_delete);
                    deleteConfirmBuilder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            service.deleteAllAlarms();

                            adapter.removeAll();

                            setEmptyViewIfEmpty(getActivity());

                            dismiss();
                        }
                    });

                    deleteConfirmBuilder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return deleteConfirmBuilder.create();
                case ActivityAlarmClock.DELETE_ALARM_CONFIRM:
                    final AlertDialog.Builder deleteAlarmConfirmBuilder =
                            new AlertDialog.Builder(getActivity());

                    deleteAlarmConfirmBuilder.setTitle(R.string.delete);
                    deleteAlarmConfirmBuilder.setMessage(
                            R.string.confirm_delete);

                    deleteAlarmConfirmBuilder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    removeItemFromList(getActivity(),
                                            getArguments().getLong("alarmId"),
                                            getArguments().getInt("position"));

                                    dismiss();
                                }
                            });

                    deleteAlarmConfirmBuilder.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dismiss();
                                }
                            });
                    return deleteAlarmConfirmBuilder.create();
                default:
                    return super.onCreateDialog(savedInstanceState);
            }
        }

    }

    private void showPickerClock(){
        Calendar now = Calendar.getInstance();
        picker = TimePickerDialog.newInstance(
                ActivityAlarmClock.this,
                ActivityAlarmClock.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(ActivityAlarmClock.this)
        );

        if (AppSettings.isThemeDark(ActivityAlarmClock.this)) {
            picker.setThemeDark(true);
        }

        picker.setAccentColor(AppSettings.getTimePickerColor(
                ActivityAlarmClock.this));

        picker.vibrate(true);

        if (AppSettings.isDebugMode(ActivityAlarmClock.this)) {
            picker.enableSeconds(true);
        } else {
            picker.enableSeconds(false);
        }

        AlarmTime time = new AlarmTime(now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), 0);

        picker.setTitle(time.timeUntilString(ActivityAlarmClock.this));
        picker.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
