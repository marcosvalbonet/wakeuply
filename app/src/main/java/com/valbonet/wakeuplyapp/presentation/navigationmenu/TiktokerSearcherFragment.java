package com.valbonet.wakeuplyapp.presentation.navigationmenu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.model.search.Muser;
import com.valbonet.wakeuplyapp.presentation.PlayUserActivity;
import com.valbonet.wakeuplyapp.presentation.PlayUserWebActivity;
import com.valbonet.wakeuplyapp.presentation.presenter.SearchUserPresenter;
import com.valbonet.wakeuplyapp.presentation.searcher.Lead;
import com.valbonet.wakeuplyapp.presentation.searcher.LeadsAdapter;
import com.valbonet.wakeuplyapp.presentation.searcher.adapter.MuserAdapter;
import com.valbonet.wakeuplyapp.presentation.searcher.listener.OnClickSearchListener;
import com.valbonet.wakeuplyapp.presentation.searcher.listener.OnQueryTextListener;
import com.valbonet.wakeuplyapp.usecases.GetMusersUseCase;
import com.valbonet.wakeuplyapp.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TiktokerSearcherFragment extends Fragment implements SearchUserPresenter.View{

    private ListView listView;
    private MuserAdapter muserAdapter;
    LeadsAdapter leadsAdapter;

    private SearchUserPresenter searchUserPresenter;
    private GetMusersUseCase getMusersUseCase;

    public static ArrayList<Muser> musersList = new ArrayList<>();
    public static int listSize = 100;
    public static int nbItems = 100;

    public static TiktokerSearcherFragment newInstance() {
        TiktokerSearcherFragment fragment = new TiktokerSearcherFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getMusersUseCase = new GetMusersUseCase();
        searchUserPresenter = new SearchUserPresenter(this, getMusersUseCase);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.fg_leads_list);

        // Deprecated
        leadsAdapter = new LeadsAdapter(getActivity(), new ArrayList<Lead>(), R.layout.list_item_lead_fragment);
        muserAdapter = new MuserAdapter(getActivity(), R.layout.list_item_lead_fragment, new ArrayList<Muser>());
        // Assign adapter to ListView
        listView.setAdapter(muserAdapter);

        listView.setOnItemClickListener(new OnClickSearchListener(getActivity(), listView));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int first = view.getLastVisiblePosition();
                int count = view.getChildCount();

                if (first + count > listSize) {
                    int offset = listSize;
                    listSize += nbItems;
                    searchUserPresenter.getMoreMusers(String.valueOf(offset), String.valueOf(nbItems));
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //we don't need this method, so we leave it empty
            }
        });

        if(musersList.isEmpty()) {
            searchUserPresenter.onCreate(String.valueOf(listSize));
        }else{
            refreshView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tiktoker_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        //super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.menu_item_new);
        item.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.action_app_settings);
        item2.setVisible(false);
        MenuItem item3 = menu.findItem(R.id.action_app_share);
        item3.setVisible(false);
        MenuItem item4 = menu.findItem(R.id.action_app_premium);
        item4.setVisible(false);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchMenuItem.setIcon(R.drawable.search_16);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Toast.makeText(this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Toast.makeText(this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
                refreshView();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new OnQueryTextListener(getActivity(), searchUserPresenter));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchUserPresenter.onDestroy();
    }

    private void refreshView(){
        muserAdapter.refreshLeads(musersList);
    }

    private void addMoreItems() {
        int offset = listSize;
        listSize += nbItems;
        searchUserPresenter.getMoreMusers(String.valueOf(offset), String.valueOf(nbItems));
    }

    @Override
    public void renderMusers(@NotNull List<Muser> musers) {
        musersList.addAll(musers);
        muserAdapter.addLeads(musers);
    }

    @Override
    public void renderMusersFound(@NotNull List<Muser> musers, String nickname) {
        if (musers.isEmpty()){
            if (nickname.startsWith("@")) {
                //TODO: is necessary set to GONE this field?
//                activity.findViewById(R.id.muserfound).setVisibility(View.GONE);
                goToPlayUserActivity(nickname);

            }else {
                showNotFoundMessage();
            }
        }else {
            if (nickname.startsWith("@")) {
                if (searchUserPresenter.isNicknameInList(musers, nickname)) {
                    muserAdapter.refreshLeads(musers);
                }else{
                    goToPlayUserActivity(nickname);
                }
            }else {
                muserAdapter.refreshLeads(musers);
            }
        }
    }

    @Override
    public void goToPlayUserActivity(String nickname) {
//        Intent myIntent = new Intent(getActivity().getApplicationContext(), PlayUserWebActivity.class);
        Intent myIntent = new Intent(getActivity().getApplicationContext(), PlayUserActivity.class);
        myIntent.putExtra("nickUser", nickname);
        myIntent.putExtra("urlUser", Constants.tiktokURL + nickname);
        myIntent.putExtra("isNewURL", true);
        if (getActivity().getIntent().getExtras()!= null && getActivity().getIntent().getExtras().containsKey("alarmID")){
            myIntent.putExtra("alarmID", getActivity().getIntent().getExtras().getLong("alarmID"));
        }
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().getApplicationContext().startActivity(myIntent);
    }

    @Override
    public void showNotFoundMessage() {
        getActivity().findViewById(R.id.fg_muserfound).setVisibility(View.GONE);
        getActivity().findViewById(R.id.fg_notfound).setVisibility(View.VISIBLE);
    }
}
