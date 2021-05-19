package com.valbonet.wakeuplyapp.presentation.searcher;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.valbonet.wakeuplyapp.Constants;
import com.valbonet.wakeuplyapp.R;
import com.valbonet.wakeuplyapp.model.search.Muser;
import com.valbonet.wakeuplyapp.presentation.PlayUserWebActivity;
import com.valbonet.wakeuplyapp.presentation.presenter.SearchUserPresenter;
import com.valbonet.wakeuplyapp.presentation.searcher.adapter.MuserAdapter;
import com.valbonet.wakeuplyapp.usecases.GetMusersUseCase;
import com.valbonet.wakeuplyapp.utils.Utils;
import com.valbonet.wakeuplyapp.data.connection.Data;
import com.valbonet.wakeuplyapp.presentation.navigationmenu.TiktokerSearcherFragment;
import com.valbonet.wakeuplyapp.presentation.searcher.listener.OnClickSearchListener;
import com.valbonet.wakeuplyapp.presentation.searcher.listener.OnQueryTextListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class SearcherUserActivity extends AppCompatActivity implements SearchUserPresenter.View{

    private ListView listView;
    LeadsAdapter leadsAdapter;
    private MuserAdapter muserAdapter;

    private int listSize = TiktokerSearcherFragment.listSize;
    public static int nbItems = TiktokerSearcherFragment.nbItems;

    private SearchUserPresenter searchUserPresenter;
    private GetMusersUseCase getMusersUseCase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        if (!Utils.isConnectingToInternet(this)){
            Toast.makeText(getApplicationContext(),
                    R.string.connection_needed,
                      Toast.LENGTH_LONG)
                        .show();
            finish() ;
        }

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.leads_list);

//        leadsAdapter = new LeadsAdapter(getApplicationContext(), new ArrayList<Lead>());
        muserAdapter = new MuserAdapter(getApplicationContext(), R.layout.list_item_lead, new ArrayList<Muser>());
        // Assign adapter to ListView
        listView.setAdapter(muserAdapter);
        listView.setOnItemClickListener(new OnClickSearchListener(this, listView));

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

        getMusersUseCase = new GetMusersUseCase();
        searchUserPresenter = new SearchUserPresenter(this, getMusersUseCase);
        searchUserPresenter.onCreate(String.valueOf(listSize));

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
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
                onStart();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new OnQueryTextListener(this, searchUserPresenter));

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNotFoundMessage();
    }

    @Override
    public void showNotFoundMessage(){
        findViewById(R.id.muserfound).setVisibility(View.GONE);
        findViewById(R.id.notfound).setVisibility(View.VISIBLE);
    }

    private void hideNotFoundMessage(){
        findViewById(R.id.muserfound).setVisibility(View.VISIBLE);
        findViewById(R.id.notfound).setVisibility(View.GONE);
    }

    @Override
    public void renderMusers(@NotNull List<Muser> musers) {
        muserAdapter.addLeads(musers);
    }

    @Override
    public void renderMusersFound(@NotNull List<Muser> musers, String nickname) {

        if (musers.isEmpty()){
            if (nickname.startsWith("@") || !nickname.contains(" ")) {
                //TODO: is necessary set to GONE this field?
//                activity.findViewById(R.id.muserfound).setVisibility(View.GONE);
                goToPlayUserActivity(Constants.tiktokURL + nickname);

            }else {
                showNotFoundMessage();
            }
        }else {
            if (nickname.startsWith("@")) {
                if (searchUserPresenter.isNicknameInList(musers, nickname)) {
                    muserAdapter.refreshLeads(musers);
                }else{
                    goToPlayUserActivity(Constants.tiktokURL + nickname);
                }
            }else {
                muserAdapter.refreshLeads(musers);
            }
        }

    }

    @Override
    public void goToPlayUserActivity(@Nullable String url) {
        Intent myIntent = new Intent(getApplicationContext(), PlayUserWebActivity.class);
//        Intent myIntent = new Intent(activity.getApplicationContext(), PlayUserActivity.class);
        myIntent.putExtra("urlUser", url);
        myIntent.putExtra("isNewURL", true);
        if (getIntent().getExtras()!= null && getIntent().getExtras().containsKey("alarmID")){
            myIntent.putExtra("alarmID", getIntent().getExtras().getLong("alarmID"));
        }
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(myIntent);
    }

}
