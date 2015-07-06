package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.FavoritesAdapter;
import com.ooredoo.bizstore.model.Deal;

import java.util.List;

public class MyDealsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_deals);

        init();
    }

    private void init() {

        setupToolbar();

        Select select = new Select();

        List<Deal> deals = select.all().from(Deal.class).where("isFavorite = 1").execute();

        FavoritesAdapter adapter = new FavoritesAdapter(this, R.layout.list_item_deal, deals);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        toggleEmptyView(deals.size());
    }

    public void toggleEmptyView(int count) {
        boolean isEmpty = count == 0;
        findViewById(R.id.list_view).setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        findViewById(R.id.no_data_view).setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.tv_no_data)).setText("No favorite item found.");
        ((ImageView) findViewById(R.id.ic_no_data)).setImageResource(R.drawable.recent_searches);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}