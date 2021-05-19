package com.valbonet.wakeuplyapp.presentation.searcher.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.valbonet.wakeuplyapp.model.search.Muser;
import com.valbonet.wakeuplyapp.presentation.PlayUserActivity;
import com.valbonet.wakeuplyapp.presentation.PlayUserWebActivity;
import com.valbonet.wakeuplyapp.data.connection.HttpUpdateTiktokMuserFound;
import com.valbonet.wakeuplyapp.presentation.searcher.Lead;
import com.valbonet.wakeuplyapp.utils.UrlUtils;

import java.util.concurrent.ExecutionException;

public class OnClickSearchListener implements AdapterView.OnItemClickListener {

    Activity activity;
    ListView listView;

    public OnClickSearchListener(Activity activity, ListView listView){
        this.activity = activity;
        this.listView = listView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {

        // ListView Clicked item value
        Muser itemValue = (Muser) listView.getItemAtPosition(position);

        try{
            HttpUpdateTiktokMuserFound muserFound = new HttpUpdateTiktokMuserFound();
            muserFound.execute(itemValue.getNickname()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent myIntent;
//        if (itemValue.getUserId() ==null || itemValue.getUserId().isEmpty()){
//            myIntent = new Intent(activity, PlayUserWebActivity.class);
//        }else{
            myIntent = new Intent(activity, PlayUserActivity.class);
//        }

        myIntent.putExtra("nameUser", itemValue.getName());
        myIntent.putExtra("nickUser", itemValue.getNickname());
        myIntent.putExtra("userId", itemValue.getUserId());
        myIntent.putExtra("secUid", itemValue.getSecUid());
        myIntent.putExtra("urlUser", new UrlUtils().createMuserURL(itemValue.getNickname()));
        if (activity.getIntent().getExtras()!= null && activity.getIntent().getExtras().containsKey("alarmID")){
            myIntent.putExtra("alarmID", activity.getIntent().getExtras().getLong("alarmID"));
        }
        //myIntent.putExtra("alarmID", getActivity().getIntent().getExtras().getLong("alarmID"));
        myIntent.putExtra("userImgURL", itemValue.getAvatarMedium());
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(myIntent);

    }
}
