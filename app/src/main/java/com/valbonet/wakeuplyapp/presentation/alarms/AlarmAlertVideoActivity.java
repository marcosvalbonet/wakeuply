package com.valbonet.wakeuplyapp.presentation.alarms;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.ads.AudienceNetworkAds;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.utils.Wakeuply_Saving;
import com.valbonet.wakeuplyapp.presentation.WebViewVideo;
import com.valbonet.wakeuplyapp.presentation.alarmclock.ActivityAlarmClock;
import com.valbonet.wakeuplyapp.presentation.alarmclock.AlarmSettings;
import com.valbonet.wakeuplyapp.presentation.alarmclock.AlarmUtil;
import com.valbonet.wakeuplyapp.presentation.alarmclock.DbAccessor;
import com.valbonet.wakeuplyapp.presentation.alarmclock.MusicControl;
import com.valbonet.wakeuplyapp.presentation.alarmclock.ReceiverAlarm;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.data.connection.Events;
import com.valbonet.wakeuplyapp.presentation.ads.mediators.AlarmFacebookInterstitial;
import com.valbonet.wakeuplyapp.presentation.ads.mediators.AlarmFacebookNativeAsInterstitial;

import java.util.Calendar;

public class AlarmAlertVideoActivity extends Activity implements OnCompletionListener, View.OnClickListener {

	String TAG = "AlarmAlertVideoActivity";

	private boolean alarmActive;
	private Vibrator vibrator;

	private WebView webView;
	private VideoView vidView;

	private DbAccessor db;
	private Uri alarmUri;
	private AlarmSettings settings;

	AlarmFacebookInterstitial alarmFacebookInterstitial;
	AlarmFacebookNativeAsInterstitial alarmFacebookNativeAsInterstitial;
	Wakeuply_Saving wakeuply_saving;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

		Log.d(TAG, "window: ");
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.alarm_alert_video);

//		Bundle bundle = this.getIntent().getExtras();

		db = new DbAccessor(getApplicationContext());
		alarmUri = this.getIntent().getData();

        if (alarmUri != null) {
			long alarmId = AlarmUtil.alarmUriToId(alarmUri);
			settings = db.readAlarmSettings(alarmId);
			if (settings.getAlarmUserID().equals("no_mail") || settings.getAlarmUserID().equals("")){
				db.writeAlarmSettings(alarmId, settings);
			}
		}else{
			settings = new AlarmSettings(getApplicationContext());
		}

		//Stop notification
		int notificationId = this.getIntent().getIntExtra("com.my.app.notificationId", 0);
		Log.d(TAG, "notificationId: "+notificationId);
		/* Your code to handle the event here */
		if (notificationId != 0){
			NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel( notificationId ) ;
			MusicControl.getInstance(getApplicationContext()).stopMusic();
		}

		Log.d(TAG, "MusicControl: ");

		//Add the alarm name in the title of view
		((Button) findViewById(R.id.snooze)).setOnClickListener(this);
		((Button) findViewById(R.id.closeAlarm)).setOnClickListener(this);
		
		vidView = findViewById(R.id.myVideo);
		vidView.setOnCompletionListener(this);
		vidView.requestFocus();
		vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				vidView.start();
			}
		});

		vidView.setOnErrorListener(vidVwErrorListener);

		webView = findViewById(R.id.webViewer);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/600.7.12 (KHTML, like Gecko) Version/8.0.7 Safari/600.7.12";
        webView.getSettings().setUserAgentString(userAgent);

		if (!isAirplaneModeOn(this) && isConnectingToInternet()) {

			if (settings != null && null != settings.getVideoAlarmUrl()){
				String videoUrl = settings.getVideoAlarmUrl();
				if (settings.getRandom()){
					// getRandomVideo
					String videoPage = Data.getRandomTiktokMuserVideos(settings.getNickMuser());
					if (videoPage != null) videoUrl = videoPage;
				}

				//TODO: Data.getNoWaterMarkVideo
				// webview.loadUrl  con WebClientNoWaterMark  y pasar vidView donde se ejecutará el video
				// POner userAgent okhttp

				WebViewVideo webViewVideo =  new WebViewVideo(this, webView, vidView);
				AlarmSettings originalSettings = db.readAlarmSettings(-1);
				webViewVideo.setVolume(originalSettings.getVolumePercent());
				webViewVideo.loadURL(videoUrl);

			}else {
				startAlarmWithoutInternet();
			}

			if (settings != null && settings.getVibrate()) {
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				long[] pattern = { 1000, 200, 200, 200 };
				vibrator.vibrate(pattern, 0);
			}

		}else {
			startAlarmWithoutInternet();
		}

		wakeuply_saving = new Wakeuply_Saving(this);

		if (!wakeuply_saving.isPremium()){
		/*
		 * Remove this lines because a problem with google play store
		 */
			// Initialize the Audience Network SDK
//			AudienceNetworkAds.initialize(this);
//
//			alarmFacebookInterstitial = new AlarmFacebookInterstitial(this);
//			alarmFacebookInterstitial.init();
//
//			alarmFacebookNativeAsInterstitial = new AlarmFacebookNativeAsInterstitial(this);
//			alarmFacebookNativeAsInterstitial.init();

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;
	}

	
	private void startAlarmWithoutInternet() {

		findViewById(R.id.progressBar_cyclic).setVisibility(View.GONE);

		long file = getTiktokVideo();

		AlarmSettings originalSettings = db.readAlarmSettings(-1);

		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);  //this will return current volume.
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Utils.get15Volume(originalSettings.getVolumePercent()), AudioManager.FLAG_PLAY_SOUND);

		String uri = "android.resource://" + getPackageName() + "/raw/" + file;
		Uri path = Uri.parse(uri);
		vidView.setVideoURI(path);
		vidView.start();

	}

	private MediaPlayer.OnErrorListener vidVwErrorListener = new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			//if there was an error in trying to play the intro video

			long file = getTiktokVideo();

			String uri = "android.resource://" + getPackageName() + "/raw/" + file;
			Uri path = Uri.parse(uri);
			vidView.setVideoURI(path);
			vidView.start();

			return true;
		}
	};

	private long getTiktokVideo(){
		int nbfile = (int)(Math.random() * 7) + 1;
		long video;

		switch (nbfile){
			case 1:
				video = R.raw.tiktokvideo1;
				break;
			case 2:
				video = R.raw.tiktokvideo2;
				break;
			case 3:
				video = R.raw.tiktokvideo3;
				break;
			case 4:
				video = R.raw.tiktokvideo4;
				break;
			case 5:
				video = R.raw.tiktokvideo5;
				break;
			case 6:
				video = R.raw.tiktokvideo6;
				break;
			case 7:
				video = R.raw.tiktokvideo7;
				break;
			default:
				video = R.raw.tiktokvideo1;
		}

		return video;
	}

	/*
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}

	/*
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	//	StaticWakeLock.lockOff(this);
	}

	@Override
	protected void onDestroy() {
		try {
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			vidView.stopPlayback();
		} catch (Exception e) {

		}
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		if (!alarmActive)
			return;
		String button = (String) v.getTag();
		
		//v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		
		if (button.equalsIgnoreCase("snooze")) {
			
			Button buttonClick = (Button) findViewById(R.id.snooze);
			buttonClick.setEnabled(false);

			vidView.pause();
			finish();

			Intent intent = new Intent(getApplicationContext(), ReceiverAlarm.class);
			intent.setData(alarmUri);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.MINUTE, settings.getSnoozeMinutes());

			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

			//TODO: Update this call to the new System AlarmClock
			AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
			alarmManager.setAlarmClock(info, pendingIntent);

			Toast.makeText(this, AlarmUtil.getTimeUntilNextAlarmMessage(calendar.getTimeInMillis()), Toast.LENGTH_LONG).show();
			
		} else if (button.equalsIgnoreCase("closeAlarm")) {
			vidView.pause();

			addNewEvent();
			
			if (wakeuply_saving.isPremium()){
				Intent myIntent = new Intent(v.getContext(), ActivityAlarmClock.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				v.getContext().startActivity(myIntent);
				//finish();
			}else{

                if(isAirplaneModeOn(this) || !isConnectingToInternet() ){

					Intent myIntent = new Intent(v.getContext(), ActivityAlarmClock.class);
					myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					v.getContext().startActivity(myIntent);

				}else if (alarmFacebookNativeAsInterstitial != null && !alarmFacebookNativeAsInterstitial.hasError()){
					//Jump to AlarmFacebookNativeAsInterstitial if it is lock screen
//					alarmFacebookNativeAsInterstitial.show();

				}else if (alarmFacebookInterstitial != null && !alarmFacebookInterstitial.hasError()){
					//Jump to AlarmFacebookInterstitial if not Lock screen
//					alarmFacebookInterstitial.show();

				}else{

					Intent myIntent = new Intent(v.getContext(), ActivityAlarmClock.class);
					myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(myIntent);

				}
			}

		}
	}


	@Override
	public void onCompletion(MediaPlayer mp) {

		mp.start();

	}
	
	private float getFloatVolume(int volume) {
		Float progress = volume *1.0f;
		return progress/100.0f;
	}

	public static boolean isAirplaneModeOn(Context context){
		return Settings.System.getInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}

	public boolean isConnectingToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}

	private void addNewEvent(){
		Boolean showAds = false;
		if (alarmFacebookNativeAsInterstitial != null && !alarmFacebookNativeAsInterstitial.hasError()){
			showAds = true;
		}

		Events.logEvent(Events.ALARM_RUN, "userPremium:"+wakeuply_saving.isPremium()+" showAds:"+showAds);

	}
	
}
