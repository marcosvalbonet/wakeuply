package com.valbonet.wakeuplyapp.presentation.navigationmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.model.Tiktok;
import com.valbonet.wakeuplyapp.presentation.view.WatchViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class WatchFragment extends Fragment {

    private WatchViewPagerAdapter pageAdapter;
    private ArrayList<Tiktok> list;
    private ViewPager2 viewPager2;

    public static List<Tiktok> randomTiktokList = new ArrayList<Tiktok>();

    public static WatchFragment newInstance() {
        WatchFragment fragment = new WatchFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        //view.getSupportActionBar().hide();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        viewPager2 = view.findViewById(R.id.viewPager2);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        list = new ArrayList<Tiktok>(200);
        list.addAll(randomTiktokList);

        pageAdapter = new WatchViewPagerAdapter(view.getContext(), viewPager2, list);
        pageAdapter.setViewPager(viewPager2);
        viewPager2.setAdapter(pageAdapter);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.registerOnPageChangeCallback(
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    pageAdapter.play(position);
                }
            }
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.playvideopageradapter, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadTiktoks();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void loadTiktoks(){
        new Thread((new Runnable() {
            @Override
            public void run() {
                List<Tiktok> tiktokList = Data.getRandomTiktokMusers(2);
                for(Tiktok tiktok : tiktokList){
                    list.add(tiktok);
                    System.out.println("New video added: "+tiktok.getVideo().getVideoURLPage());
                }

                //TODO MVAL: Make better this creation of empty tiktok list
                for (int i=0;  i< 200; i++){
                    Tiktok tiktok = new Tiktok(null, null);
                    list.add(tiktok);
                }

                pageAdapter.setList(list);

                randomTiktokList = Data.getRandomTiktokMusers(2);

            }
        })).start();
        // pageAdapter.notifyDataSetChanged();
    }
}
