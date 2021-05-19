package com.valbonet.wakeuplyapp.data.connection;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;


public class Events {

    public static String ALARM_RUN = "ALARM_RUN";
    public static String SHARE = "SHARE";
    public static String PREMIUM = "PREMIUM";


    public static boolean logEvent(String event, String info){
        HttpAddNewTiktokEvent addEvent = new HttpAddNewTiktokEvent();
        try {
            String request = addEvent.execute(event, info).get();

            JSONObject jsonObject = new JSONObject(request);
            int status = jsonObject.getInt("status");
            if (status == 1){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
