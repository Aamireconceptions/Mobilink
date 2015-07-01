package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.RecentSearchesAdapter;
import com.ooredoo.bizstore.model.SearchItem;

import java.util.List;

public class RecentSearchesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recent_searches);

        init();
    }

    private void init() {
        setupToolbar();

        List<SearchItem> searchItems = new Select().all().from(SearchItem.class).execute();

        ListView listView = (ListView) findViewById(R.id.lv_recent_searches);

        RecentSearchesAdapter adapter = new RecentSearchesAdapter(this, R.layout.list_item_recent_search, searchItems);

        listView.setAdapter(adapter);

        boolean noSearchItemFound = searchItems.size() == 0;
        listView.setVisibility(noSearchItemFound ? View.GONE : View.VISIBLE);
        findViewById(R.id.no_data_view).setVisibility(noSearchItemFound ? View.VISIBLE : View.GONE);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
