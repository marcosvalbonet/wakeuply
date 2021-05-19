package com.valbonet.wakeuplyapp.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;

import com.valbonet.wakeuplyapp.R;

import java.util.List;

public class SpecialConfigurationHelper {

    public static String PREF_KEY_APP_AUTO_START = "app_auto_start";

    public static String PREF_KEY_OTHER_PREFERENCES = "other_preferences";
    /***
     * Xiaomi
     */
    private final String BRAND_XIAOMI = "xiaomi";
    private String PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter";
    private String PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity";
    private String PACKAGE_XIAOMI_COMPONENT_PERMISSIONS = "com.miui.permcenter.permissions.PermissionsEditorActivity";

    /***
     * Letv
     */
    private final String BRAND_LETV = "letv";
    private String PACKAGE_LETV_MAIN = "com.letv.android.letvsafe";
    private String PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManageActivity";

    /***
     * ASUS ROG
     */
    private final String BRAND_ASUS = "asus";
    private String PACKAGE_ASUS_MAIN = "com.asus.mobilemanager";
    private String PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings";

    /***
     * Honor
     */
    private final String BRAND_HONOR = "honor";
    private String PACKAGE_HONOR_MAIN = "com.huawei.systemmanager";
    private String PACKAGE_HONOR_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity";

    /**
     * Oppo
     */
    private final String BRAND_OPPO = "oppo";
    private String PACKAGE_OPPO_MAIN = "com.coloros.safecenter";
    private String PACKAGE_OPPO_FALLBACK = "com.oppo.safe";
    private String PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity";
    private String PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity";
    private String PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity";

    /**
     * Vivo
     */

    private final String BRAND_VIVO = "vivo";
    private String PACKAGE_VIVO_MAIN = "com.iqoo.secure";
    private String PACKAGE_VIVO_FALLBACK = "com.vivo.perm;issionmanager";
    private String PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity";
    private String PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity";
    private String PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager";

    /**
     * Nokia
     */

    private final String BRAND_NOKIA = "nokia";
    private String PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3";
    private String PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity";


    private SpecialConfigurationHelper() {
    }

    public static SpecialConfigurationHelper getInstance() {
        return new SpecialConfigurationHelper();
    }


    public void getAutoStartPermission(Context context) {

        if (Utils.readBooleanValue(context, PREF_KEY_APP_AUTO_START)){
            return ;
        }

        String build_info = Build.BRAND.toLowerCase();
        switch (build_info) {
            case BRAND_ASUS:
                autoStartAsus(context);
                break;
            case BRAND_XIAOMI:
                autoStartXiaomi(context);
                break;
            case BRAND_LETV:
                autoStartLetv(context);
                break;
            case BRAND_HONOR:
                autoStartHonor(context);
                break;
            case BRAND_OPPO:
                autoStartOppo(context);
                break;
            case BRAND_VIVO:
                autoStartVivo(context);
                break;
            case BRAND_NOKIA:
                autoStartNokia(context);
                break;

        }

    }

    public void getOtherPermission(Context context) {

        if (Utils.readBooleanValue(context, PREF_KEY_OTHER_PREFERENCES)){
            return ;
        }

        String build_info = Build.BRAND.toLowerCase();
        switch (build_info) {
            case BRAND_ASUS:
                //autoStartAsus(context);
                break;
            case BRAND_XIAOMI:
                otherPreferencesXiaomi(context);
                break;
            case BRAND_LETV:
                //autoStartLetv(context);
                break;
            case BRAND_HONOR:
                //autoStartHonor(context);
                otherPreferencesHuawei(context);
                break;
            case BRAND_OPPO:
                otherPreferencesOppo(context);
                break;
            case BRAND_VIVO:
                //autoStartVivo(context);
                break;
            case BRAND_NOKIA:
                //autoStartNokia(context);
                break;

        }

    }

    public void resetPreferences(Context context){
        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, false);
        Utils.saveBooleanValue(context, PREF_KEY_OTHER_PREFERENCES, false);
    }

    private void showAutoStartAlert(Context context, DialogInterface.OnClickListener onClickListener) {

        new AlertDialog.Builder(context).setTitle(context.getString(R.string.settings_autostart_title))
                .setMessage(context.getString(R.string.settings_autostart_message))
                .setPositiveButton(context.getString(R.string.allow), onClickListener).show().setCancelable(false);
    }

    private void showOtherPreferencesAlert(Context context, DialogInterface.OnClickListener onClickListener) {

        new AlertDialog.Builder(context).setTitle(context.getString(R.string.settings_other_permmission_title))
                .setMessage(context.getString(R.string.settings_other_permmission_message))
                .setPositiveButton(context.getString(R.string.allow), onClickListener).show().setCancelable(false);
    }

    private void autoStartAsus(final Context context) {
        if (isPackageExists(context, PACKAGE_ASUS_MAIN)) {

            showAutoStartAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        SpecialConfigurationHelper.this.startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });

        }


    }

    private void autoStartXiaomi(final Context context) {
        if (isPackageExists(context, PACKAGE_XIAOMI_MAIN)) {
            showAutoStartAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        SpecialConfigurationHelper.this.startIntent(context, PACKAGE_XIAOMI_MAIN, PACKAGE_XIAOMI_COMPONENT);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }

    private void autoStartLetv(final Context context) {
        if (isPackageExists(context, PACKAGE_LETV_MAIN)) {
            showOtherPreferencesAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        startIntent(context, PACKAGE_LETV_MAIN, PACKAGE_LETV_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }

    private void autoStartHonor(final Context context) {
        if (isPackageExists(context, PACKAGE_HONOR_MAIN)) {
            showAutoStartAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        startIntent(context, PACKAGE_HONOR_MAIN, PACKAGE_HONOR_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }

    private void autoStartOppo(final Context context) {
        if (isPackageExists(context, PACKAGE_OPPO_MAIN) || isPackageExists(context, PACKAGE_OPPO_FALLBACK)) {
            showAutoStartAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                            startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            try {
                                Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                                startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_FALLBACK_A);
                            } catch (Exception exx) {
                                exx.printStackTrace();
                            }

                        }

                    }
                }
            });


        }
    }

    private void autoStartVivo(final Context context) {
        if (isPackageExists(context, PACKAGE_VIVO_MAIN) || isPackageExists(context, PACKAGE_VIVO_FALLBACK)) {
            showAutoStartAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                            startIntent(context, PACKAGE_VIVO_FALLBACK, PACKAGE_VIVO_COMPONENT_FALLBACK);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            try {
                                Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                                startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT_FALLBACK_A);
                            } catch (Exception exx) {
                                exx.printStackTrace();
                            }

                        }

                    }

                }
            });
        }
    }

    private void autoStartNokia(final Context context) {
        if (isPackageExists(context, PACKAGE_NOKIA_MAIN)) {
            showAutoStartAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        startIntent(context, PACKAGE_NOKIA_MAIN, PACKAGE_NOKIA_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void otherPreferencesXiaomi(final Context context) {
        if (isPackageExists(context, PACKAGE_XIAOMI_MAIN)) {
            if (Utils.readBooleanValue(context, PREF_KEY_OTHER_PREFERENCES)){
                return ;
            }
            showOtherPreferencesAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_OTHER_PREFERENCES, true);

                        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                        int versionCode = RomUtils.getMiuiVersion();
                        if (versionCode == 6 || versionCode == 7) {
                            //MIUI V6 y MIUI V7
                            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                        }else{
                            //MIUI V8
                            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                        }
                        intent.putExtra("extra_pkgname", context.getPackageName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });


        }
    }

    private void otherPreferencesHuawei(final Context context) {
        if (isPackageExists(context, PACKAGE_HONOR_MAIN)) {
            showAutoStartAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    try {
                        Utils.saveBooleanValue(context, PREF_KEY_APP_AUTO_START, true);
                        double versionCode = RomUtils.getEmuiVersion();
                        if (versionCode == 3.1) {
                            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
                            intent.setComponent(comp);
                        } else {
                            //emui 3.0
                            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
                            intent.setComponent(comp);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ComponentName comp = new ComponentName("com.huawei.systemmanager",
                                "com.huawei.permissionmanager.ui.MainActivity");
                        intent.setComponent(comp);
                        context.startActivity(intent);
                    }
                }
            });


        }

    }

    private void otherPreferencesOppo(Context context) {
        //merge request from https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //com.coloros.safecenter/.sysfloatwindow.FloatWindowListActivity
            ComponentName comp = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            context.startActivity(intent);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private void startIntent(Context context, String packageName, String componentName) throws Exception {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, componentName));
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            throw var5;
        }
    }

    private Boolean isPackageExists(Context context, String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo :
                packages) {
            if (packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }

        return false;
    }
}
