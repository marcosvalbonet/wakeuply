package com.valbonet.wakeuplyapp.presentation.searcher.listener;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import com.valbonet.wakeuplyapp.presentation.PlayUserWebActivity;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.presentation.presenter.SearchUserPresenter;
import com.valbonet.wakeuplyapp.presentation.searcher.SearcherUserActivity;

import java.util.ArrayList;

public class OnQueryTextListener implements SearchView.OnQueryTextListener{
    Activity activity;
    SearchUserPresenter searchUserPresenter;

    public OnQueryTextListener(Activity activity, SearchUserPresenter searchUserPresenter){
        this.activity = activity;
        this.searchUserPresenter = searchUserPresenter;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.d("TAG", "onQueryTextSubmit ");

        hideNotFoundMessage();

        if (s.contains("http")){
                    /*TODO: Si s contiene una url("http") llamar a HttpAddNewTiktokMuser y
                        saltar a PlayUserActivity_old con la url and whether is a User account
                        launch URL to webView with url and get nickname, url, image and name
                        Add in our ddbb HttpAddNewTiktokMuser
                    */
            String url = s.substring(s.indexOf("http"), s.lastIndexOf("/"));


            Intent myIntent = new Intent(activity.getApplicationContext(), PlayUserWebActivity.class);
            myIntent.putExtra("urlUser", url);
            myIntent.putExtra("isNewURL", true);
            if (activity.getIntent().getExtras()!= null && activity.getIntent().getExtras().containsKey("alarmID")){
                myIntent.putExtra("alarmID", activity.getIntent().getExtras().getLong("alarmID"));
            }
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(myIntent);

        }else{
            //Si tiene @ o no tiene nada buscamos en nuestra BBDD
            //search(s);
            searchUserPresenter.findMuserByNickname(s);
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Log.d("TAG", "onQueryTextChange ");
        return false;
    }

    private void showNotFoundMessage(){
        if (activity instanceof SearcherUserActivity){
            activity.findViewById(R.id.muserfound).setVisibility(View.GONE);
            activity.findViewById(R.id.notfound).setVisibility(View.VISIBLE);
        }else{
            activity.findViewById(R.id.fg_muserfound).setVisibility(View.GONE);
            activity.findViewById(R.id.fg_notfound).setVisibility(View.VISIBLE);
        }

    }

    private void hideNotFoundMessage(){
        if (activity instanceof SearcherUserActivity){
            activity.findViewById(R.id.muserfound).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.notfound).setVisibility(View.GONE);
        }else{
            activity.findViewById(R.id.fg_muserfound).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.fg_notfound).setVisibility(View.GONE);
        }

    }
}
