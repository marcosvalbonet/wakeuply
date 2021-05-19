package com.valbonet.wakeuplyapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.data.connection.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.UUID;

public class Utils {


    /** The mobile telecommunication technologies.  */
    public static enum NETWORK_TYPE {_2G, _3G, _4G, WiFi, UNKNOWN};

    private final static String SHARED_PREFS_FILE = "HMPrefs";

    /**
     * Gets the network type that is consuming the device. See {@link NETWORK_TYPE}.
     *
     * @param context
     * @return 	The network type, see {@link NETWORK_TYPE}. UNKNOWN is returned in case of a
     * 			non-recognized technology or in case TelephonyManager could not be get.
     */
    public static NETWORK_TYPE net_getNetworkType(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(mTelephonyManager!=null) {

//            if(net_isWifiOn(context)) {
//                return NETWORK_TYPE.WiFi;
//            }

            int networkType = mTelephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NETWORK_TYPE._2G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    /**
                     https://en.wikipedia.org/wiki/Evolution-Data_Optimized says that
                     NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_AEV-DO is an evolution
                     of the CDMA2000 (IS-2000) standard that supports high data rates.
                     CDMA2000 - https://en.wikipedia.org/wiki/CDMA2000, CDMA2000 is a
                     family of 3G[1] mobile technology standards for sending voice,
                     data, and signaling data between mobile phones and cell sites.*/
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    /**
                     * 3g HSDPA, HSPAP(HSPA+) are main network type which are under
                     * the 3g Network. But from other constants, also it will be 3G,
                     * like HSPA, HSDPA, etc which are in 3g case.
                     * Some other cases added after checking them.
                     * See https://en.wikipedia.org/wiki/4G#Data_rate_comparison */
                    return NETWORK_TYPE._3G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    /**
                     * LTE https://en.wikipedia.org/wiki/LTE_(telecommunication)
                     * (marketed as 4G LTE) */
                    return NETWORK_TYPE._4G;
                default:
                    return NETWORK_TYPE.UNKNOWN;
            }
        }else{
            Log.w("TAG", "Could not access to TelephonyManager!.");
        }

        return NETWORK_TYPE.UNKNOWN;
    }

    public static String reviewURL(String url){
        if (url.equals(new String())) return url;

        if (!url.contains("http")){
            url = "https:"+url;
        }
        if (url.contains("\"")){
            url = url.replace("\"", "");
        }
        return url;
    }

    public static String reviewVideoURL(String url){
        //https://www.tiktok.com/node/video/playwm?id=6703165002372615429&_signature=DyEjKQAAUisGbVk8U2yCmQ8hIz
        if (url.contains("/node/video/")){
            if(!url.contains("https://www.tiktok.com")){
                url = "https://www.tiktok.com" + url;
            }
        }
        return url;
    }

    public static String addHashtag(String hashtag){

        if (hashtag.startsWith("#")){
            return hashtag;
        }else{
            return "#"+hashtag;
        }
    }

    public static String removeHashtag(String hashtag){

        if (hashtag.startsWith("#")){
            return hashtag.substring(1, hashtag.length());
        }else{
            return hashtag;
        }
    }


    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static float getFloatVolume(int volume) {
        Float  progress = volume *1.0f;
        return progress/100.0f;
    }

    public static int get15Volume(int volume) {
        int progress = volume *15;
        return progress/100;
    }

    public static void saveValue(Context ctx, String key, String value){

        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREFS_FILE, 0);
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String readValue(Context ctx, String key){

        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREFS_FILE, 0);
        return preferences.getString(key, "");

    }

    public static void saveBooleanValue(Context ctx, String key, Boolean value){

        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREFS_FILE, 0);
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean readBooleanValue(Context ctx, String key){

        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREFS_FILE, 0);
        return preferences.getBoolean(key, false);

    }

    public static String getHelloByHour(Context context){

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String hello = context.getString(R.string.goodmorning);

        if(hourOfDay>= 0 && hourOfDay<12){
            hello = context.getString(R.string.goodmorning);

        }else if (hourOfDay>= 12 && hourOfDay<20){
            hello = context.getString(R.string.goodafternoon);

        }else if (hourOfDay>= 20 && hourOfDay<=23){
            hello = context.getString(R.string.goodevening);

        }else{
            hello = context.getString(R.string.goodmorning);
        }

        return hello;
    }

    /**
     * Returns a unique UUID for an android device.<br>
     * <br>
     * <b>Requires the permission "READ_PHONE_STATE"</b>. Remember to ask
     * for permissions in Android 6+ (use ToolBox.PERMISSION_LOCATION and permission
     * related methods in ToolBox before using this).<br>
     * <br>
     * If the device IMEI and the SIM IMSI are available, these values are used
     * to construct an UUID that starts with "@". If these are not available, the
     * UUID will be constructed by using as the base the ANDROID_ID only if is
     * not null and not the some device manufacturers buggy ID 9774d56d682e549c
     * for 2.2, 2.3 android version (@see http://code.google.com/p/android/issues/detail?id=10603).
     * If is not available or is the buggy one, a unique UUID will be generated
     * using the SERIAL property of the device and if not available, a bunch of
     * device properties will be used to generated a unique UUID string. In this case,
     * the UUID string will begin with "#".
     *
     * @param context
     * @return The UUID string or null if is not possible to get.
     */
    public static String device_getId(Context context) {
        String uuid = null;

        String imei = null;
        String imsi = null;
        try{
            imei = device_getIMEI(context);
            imsi = device_getSIMIMSI(context);
        }catch(SecurityException e){
                Log.w("TAG", "SecurityException (" + e.getMessage() + "). Remember to grant permissions if Android 6+.");
        }

        if((imei!=null && imei.length()>0) &&
                (imsi!=null && imsi.length()>0)){
            String uniqueIdString = imei + "/" + imsi;
            try{
                uuid = "@" + (UUID.nameUUIDFromBytes(uniqueIdString.getBytes("utf8"))).toString();
            } catch (UnsupportedEncodingException e) {
                Log.e("TAG", "UnsupportedEncodingException (" + e.getMessage() + ").", e);
            }
        }else{
            uuid = "#" + device_getLikelyId(context);
        }

        return uuid;
    }

    /**
     * Gets the device unique id, IMEI, if is available, NULL otherwise.
     * <br><br>
     * <b>Requires the permission "READ_PHONE_STATE"</b>. Remember to ask
     * for permissions in Android 6+ (use ToolBox.PERMISSION_LOCATION and permission
     * related methods in ToolBox before using this).
     *
     * @param context
     * @return
     */
    @SuppressWarnings({"MissingPermission"})
    public static String device_getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if(tm!=null) {
            return tm.getDeviceId();
        }else{
            return null;
        }

    }

    /**
     * Gets the SIM unique Id, IMSI.
     * <br><br>
     * <b>Requires the permission "READ_PHONE_STATE"</b>. Remember to ask
     * for permissions in Android 6+ (use ToolBox.PERMISSION_LOCATION and permission
     * related methods in ToolBox before using this).
     *
     * @param context
     * @return The SIM subscriberId or NULL if is not available or empty if no SIM is detected.
     */
    @SuppressWarnings({"MissingPermission"})
    public static String device_getSIMIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm!=null)
            return tm.getSubscriberId();
        else
            return null;
    }

    /**
     * Returns a unique UUID for the an android device. As with any UUIDs,
     * this unique ID is "very highly likely" to be unique across all Android
     * devices. Much more than ANDROID_ID is.
     *
     * It uses as the base the ANDROID_ID only if is not null and not the
     * some device manufacturers buggy ID 9774d56d682e549c for 2.2, 2.3 android
     * version (@see http://code.google.com/p/android/issues/detail?id=10603).
     * If is not available or is the buggy one, a unique UUID will be
     * generated using the SERIAL property of the device and if not available,
     * a bunch of device properties will be used to generated a unique UUID string.
     *
     * @param context
     * @return a UUID that may be used, in most cases, to uniquely identify your
     * 		  device for most.
     */
    private static String device_getLikelyId(Context context) {
        UUID uuid = null;

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(androidId==null) {
            uuid = generateUniqueDeviceUUIDId();
        }else{
            //Several devices by several manufacturers are affected by the ANDROID_ID bug in 2.2.
            //All affected devices have the same ANDROID_ID, which is 9774d56d682e549c. Which is
            //also the same device id reported by the emulator.
            if(!"9774d56d682e549c".equals(androidId)){
                try{
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e("TAG", "UnsupportedEncodingException (" + e.getMessage() + ").", e);
                }
            }else{
                uuid = generateUniqueDeviceUUIDId();
            }
        }

        return uuid.toString();
    }

    /**
     * Generates a unique device id using the device
     * "serial" property if is available. If not, a bunch
     * of device properties will be used to get a reliable
     * unique string key for the device.
     *
     * If there is an error in UUID generation null is
     * returned.
     *
     * @return	The unique UUID or nul in case of error.
     */
    private static UUID generateUniqueDeviceUUIDId() {
        UUID uuid = null;

        try{
            //We generate a unique id
            String serial = null;
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                serial = Build.SERIAL;
                uuid = UUID.nameUUIDFromBytes(serial.getBytes("utf8"));
            }else{
                //This bunch of data should be enough to "ensure" the
                //uniqueness.
                String m_szDevIDAlterbative = "35" + //To look like a valid IMEI
                        Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                        Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                        Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                        Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                        Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                        Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                        Build.USER.length()%10 ; //13 digits

                uuid = UUID.nameUUIDFromBytes(m_szDevIDAlterbative.getBytes("utf8"));
            }

        } catch (UnsupportedEncodingException e) {
            Log.e("TAG", "UnsupportedEncodingException (" + e.getMessage() + ").", e);
        }

        return uuid;
    }

    public static String readStream(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("VIDEOURL", "IOException", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("VIDEOURL", "IOException", e);
            }
        }
        return sb.toString();
    }

    public static boolean existNewVersionApp(Context context){

        try{
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0);
            int codeVersionServer = Integer.parseInt(Config.getCodeVersion());
            int codeVersionApp = info.versionCode;
            return  codeVersionServer > codeVersionApp;
        }catch(Exception e){
                Log.e("GET_APP_VERSION:ERROR",e.getMessage(),e);
        }
        return false;
    }

    public static void showNewVersionDialog(Context context){
        final Context fContext = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.update_dialog);

            builder.setCancelable(false)
                    .setPositiveButton(R.string.update_process_update_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            try {
                                i.setData(Uri.parse("market://details?id=" + fContext.getPackageName()));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                i.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + fContext.getPackageName()));
                            }
                            fContext.startActivity(i);
                            ((Activity) fContext).finish();
                        }
                    })
            .setNegativeButton(R.string.update_process_update_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
    }
}
