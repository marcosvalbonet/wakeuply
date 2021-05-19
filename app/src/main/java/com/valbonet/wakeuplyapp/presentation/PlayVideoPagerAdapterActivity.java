package com.valbonet.wakeuplyapp.presentation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.model.Muser;
import com.valbonet.wakeuplyapp.model.Tiktok;
import com.valbonet.wakeuplyapp.model.Video;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.presentation.view.ViewPagerAdapter;

import java.util.ArrayList;

public class PlayVideoPagerAdapterActivity extends AppCompatActivity {

    private Muser muser;
    private ViewPagerAdapter pageAdapter;
    private ArrayList<Tiktok> list;
    private ViewPager2 viewPager2;

    //private int positionView = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.playvideopageradapter);

        //Remove title bar
        getSupportActionBar().hide();

        viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        // An alarm id is required in the extras bundle.
        final Bundle bundle = getIntent().getExtras();
        muser = new Muser(bundle);
        /* TODO: MVAL Try to retrieve the cookie param. Cuando obtenemos getVideoURL,
        necesitamos el paramertro cookie con el que trabaja la web*/
        Video video = new Video(bundle);
        Video video2 = Data.getVideo(video.getVideoURLPage());

        list = new ArrayList<Tiktok>();
        list.add(new Tiktok(muser, video2));

        pageAdapter = new ViewPagerAdapter(this, viewPager2, list, muser);
        viewPager2.setAdapter(pageAdapter);
        viewPager2.setOffscreenPageLimit(2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                //Log.e("onPageScrolled", String.valueOf(position) + " " +String.valueOf(positionOffset) + " " + String.valueOf(positionOffsetPixels));
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageAdapter.play(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.e("nPageScrollStateChanged", String.valueOf(state));
            }

        });

    }


    private void loadTiktoks(final Bundle bundle){
        /*Comment to only get one video*/
        Thread thread = new Thread((new Runnable() {

            @Override
            public void run() {
                int i = 0;
                String firstVideoUrlPage = bundle.getString(Constants.videoURLPage);
                ArrayList<String> videoUrlPages = getIntent().getStringArrayListExtra(Constants.videoURLPages);
                ArrayList<String> videoUrlPagesToShow = new ArrayList<>();
                Boolean isTheNextTiktokToAdd = false;
                for(String videoUrlPage: videoUrlPages) {
                    if (firstVideoUrlPage.equals(videoUrlPage) || isTheNextTiktokToAdd) {
                        if (isTheNextTiktokToAdd){
                            videoUrlPagesToShow.add(videoUrlPage);
                        }
                        isTheNextTiktokToAdd = true;
                    }
                }

                for(String videoUrlPage: videoUrlPagesToShow) {
                    if (i < 3){
                        Video video = Data.getVideo(videoUrlPage);
                        if (video != null){
                            if(list != null) {
                                list.add(new Tiktok(muser, video));
                                i++;
                                System.out.println("New video added: " + videoUrlPage);
                            }
                        }
                    }else{
                        Video video = new Video(videoUrlPage, null, null, null);
                        if(list != null) {
                            list.add(new Tiktok(muser, video));
                        }
//                            pageAdapter.setList(list);
                    }

                }

                if(pageAdapter != null) pageAdapter.setList(list);

            }
        }));
        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!Utils.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(),
                    R.string.connection_needed,
                    Toast.LENGTH_LONG)
                    .show();
            finish() ;
        }

        loadTiktoks(getIntent().getExtras());

    }

    @Override
    protected void onDestroy() {
        viewPager2.setAdapter(null);
        list = null;
        viewPager2 = null;
        pageAdapter = null;
        super.onDestroy();
    }
}
