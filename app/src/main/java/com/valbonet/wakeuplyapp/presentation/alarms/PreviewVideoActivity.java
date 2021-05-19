package com.valbonet.wakeuplyapp.presentation.alarms;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.presentation.WebViewVideo;


public class PreviewVideoActivity extends Activity implements OnCompletionListener {

	private WebView webView;
	private VideoView vidView;
	private String videoURL;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.alarm_alert_video);

		if (!Utils.isConnectingToInternet(this)){
			Toast.makeText(getApplicationContext(),
					R.string.connection_needed,
					Toast.LENGTH_LONG)
					.show();
			finish() ;
		}

		videoURL = getIntent().getExtras().getString(Constants.videoURL);

		//Add the alarm name in the title of view

		((Button) findViewById(R.id.snooze)).setVisibility(View.GONE);
		((Button) findViewById(R.id.closeAlarm)).setVisibility(View.GONE);
		
		vidView = findViewById(R.id.myVideo);
		vidView.setOnCompletionListener(this);
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
			WebViewVideo webViewVideo =  new WebViewVideo(this, webView, vidView);
			webViewVideo.loadURL(videoURL);

		}else {

			Toast.makeText(this, "No tienes conexion a Internet", Toast.LENGTH_LONG);

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
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
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.start();
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

}
