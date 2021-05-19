package com.valbonet.wakeuplyapp.presentation.alarmclock;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Marcos on 14/11/2017.
 */

public class AlertMessages {
    private static AlertMessages instance;
    private Context context;

    public AlertMessages(Context context){
        this.context = context;
    }

    public static AlertMessages getInstance(Context context){
        if (instance == null){
            // Create the instance
            instance = new AlertMessages(context);
        }
        // Return the instance
        return instance;
    }

    public void ifHuaweiAlert() {
        final SharedPreferences settings = context.getSharedPreferences("ProtectedApps", context.MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                dontShowAgain.setText("No volver a mostrar"); //Do not show again
                dontShowAgain.setX(80);
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                //Huawei Protected Apps //requires to be enabled in 'Protected Apps' to function properly
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Aplicación Protegida")
                        .setMessage(String.format("%s requiere ser establecida como 'Aplicación Protegida' para funcionar con la pantalla apagada y poder lanzar las alarmas.%n", context.getString(com.valbonet.wakeuplyapp.R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Aplicación Protegida", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void launchUseConditions(){
        String url = "http://www.wakeuply.com/legal/es/conditions.html";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Couldn't launch the website", Toast.LENGTH_LONG).show();
        }
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = context.getSystemService(context.USER_SERVICE);
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }
}
