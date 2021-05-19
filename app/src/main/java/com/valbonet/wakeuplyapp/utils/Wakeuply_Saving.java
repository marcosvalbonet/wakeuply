package com.valbonet.wakeuplyapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Wakeuply_Saving {

	private final String SHARED_PREFS_FILE = "HMPrefs";
	private final String KEY_PREMIUM_DATE = "premiumDate";

	private final int DAYS = 10;


	private Context mContext;

	public Wakeuply_Saving(Context context){
		 mContext = context;
	}

	private SharedPreferences getSettings(){
		 return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}

	public String getPremiumDate(){
		return getSettings().getString(KEY_PREMIUM_DATE, null) == null ? "" : getSettings().getString(KEY_PREMIUM_DATE, null);
	}

	public void setPremiumNullDate(){
		Calendar today = Calendar.getInstance();
		String dateStr = "02-01-2019";


		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_PREMIUM_DATE, dateStr);
		editor.commit();
	}

	public void setPremiumDate(){
		Calendar today = Calendar.getInstance();
		SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
		String date = format1.format(today.getTime());

		SharedPreferences.Editor editor = getSettings().edit();
	    editor.putString(KEY_PREMIUM_DATE, date);
	    editor.commit();
	}

	public boolean isPremium(){
		String premDate = getPremiumDate();
		if (premDate == null || premDate.equals("")) return false;

		if (0 < getDaysLeft()) {
			return true;
		}

		return false;
	}

	public long getDaysLeft(){
		String premDate = getPremiumDate();
		if (premDate == null || premDate.equals("")) return 0;

		try{
			Calendar today = Calendar.getInstance();

//			String dateStr = "2-1-2019";
//			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
//			Date endDate = sdf2.parse(dateStr);
//			today.setTime(endDate);

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date strDate = sdf.parse(premDate);
			Calendar premiumDate = Calendar.getInstance();
			premiumDate.setTime(strDate);

			long diff = today.getTimeInMillis() - premiumDate.getTimeInMillis();
			long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

			return DAYS - Math.abs(diffDays);

		}catch (Exception e)	{
			return 0;
		}

	}

}
