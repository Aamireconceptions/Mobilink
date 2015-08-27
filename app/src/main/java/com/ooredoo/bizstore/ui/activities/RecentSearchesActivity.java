package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.RecentSearchesAdapter;
import com.ooredoo.bizstore.model.SearchItem;

import java.util.List;

public class RecentSearchesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnClearAll;

    private ListView mListView;

    private RecentSearchesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recent_searches);

        init();
    }

    private void init() {
        setupToolbar();

        List<SearchItem> searchItems = new Select().all().from(SearchItem.class).execute();

        mListView = (ListView) findViewById(R.id.lv_recent_searches);

        mAdapter = new RecentSearchesAdapter(this, R.layout.list_item_recent_search, searchItems);

        mListView.setAdapter(mAdapter);

        boolean noSearchItemFound = searchItems.size() == 0;

        btnClearAll = (Button) findViewById(R.id.btn_clear);
        btnClearAll.setOnClickListener(this);
        btnClearAll.setVisibility(noSearchItemFound ? View.GONE : View.VISIBLE);

        findViewById(R.id.no_data_view).setVisibility(noSearchItemFound ? View.VISIBLE : View.GONE);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.recent_searches));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_clear) {
            clearRecentSearches();
        }
    }

    public void clearRecentSearches() {
        List<SearchItem> searchItems = new Select().all().from(SearchItem.class).execute();
        for(SearchItem searchItem : searchItems) {
            searchItem.delete();
        }
        mAdapter.clear();
        btnClearAll.setVisibility(View.GONE);
        findViewById(R.id.no_data_view).setVisibility(View.VISIBLE);
    }

}
