package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.RecentItemsAdapter;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.RecentItem;
import com.ooredoo.bizstore.model.SearchResult;

import java.util.List;

public class RecentViewedActivity extends AppCompatActivity implements View.OnClickListener {
    private View lastSelected;

    private Button btnClearAll;

    private ListView mListView;

    private RecentItemsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recent_deals);

        init();
    }

    private void init() {
        setupToolbar();

        List<RecentItem> recentItems = new Select().all().from(RecentItem.class).execute();

        mListView = (ListView) findViewById(R.id.list_view);

        mAdapter = new RecentItemsAdapter(this, R.layout.recent_item, recentItems);

        mListView.setAdapter(mAdapter);

        int count = recentItems.size();

        toggleEmptyView(count);

        btnClearAll = (Button) findViewById(R.id.btn_clear);
        btnClearAll.setOnClickListener(this);
        btnClearAll.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
    }

    private void toggleEmptyView(int count) {
        boolean noSearchItemFound = count == 0;
        findViewById(R.id.list_view).setVisibility(noSearchItemFound ? View.GONE : View.VISIBLE);
        findViewById(R.id.no_data_view).setVisibility(noSearchItemFound ? View.VISIBLE : View.GONE);
        String msg = getString(R.string.no_recent_item);
        ((TextView) findViewById(R.id.tv_no_data)).setText(msg);
        ((ImageView) findViewById(R.id.ic_no_data)).setImageResource(R.drawable.recent_viewed);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.new_deals:
                setSelected(v);
                break;

            case R.id.popular_deals:
                setSelected(v);
                break;

            case R.id.btn_clear:
                clearRecentDeals();
                break;
        }

    }

    public void clearRecentDeals() {
        List<RecentItem> recentItems = new Select().all().from(RecentItem.class).execute();
        for(RecentItem recentItem : recentItems) {
            recentItem.delete();
        }
        mAdapter.clear();
        toggleEmptyView(0); //0 => NO_RECENT_VIEWED
        btnClearAll.setVisibility(View.GONE);
    }

    private void setSelected(View v) {
        if(lastSelected != null) {
            lastSelected.setSelected(false);
        }

        v.setSelected(true);

        lastSelected = v;
    }

    public static void addToRecentViewed(Favorite favorite) {
        if(favorite != null && favorite.id > 0) {
            RecentItem rd = new RecentItem();
            List<RecentItem> recentItems = new Select().all().from(RecentItem.class).where("itemId=" + favorite.id).execute();
            if(recentItems != null && recentItems.size() > 0) {
                rd = recentItems.get(0);
            }
            rd.id = favorite.id;
            rd.type = favorite.type;
            rd.description = favorite.description;
            rd.city = favorite.city;
            rd.title = favorite.title;
            rd.views = favorite.views;
            rd.rating = favorite.rating;
            rd.category = favorite.category;
            rd.discount = favorite.discount;
            Log.i("UPDATE", "EXISTING---" + rd.title == null ? "NULL" : rd.title);
            rd.save();
        }
    }

    public static void addToRecentViewed(SearchResult result) {
        if(result != null && result.id > 0) {
            RecentItem rd = new RecentItem();
            List<RecentItem> recentItems = new Select().all().from(RecentItem.class).where("itemId=" + result.id).execute();
            if(recentItems != null && recentItems.size() > 0) {
                rd = recentItems.get(0);
            }
            rd.id = result.id;
            rd.type = 0;
            rd.description = result.description;
            rd.city = result.location;
            rd.title = result.title;
            rd.views = result.views;
            rd.rating = result.rating;
            rd.category = "";
            rd.discount = result.discount;
            Log.i("UPDATE", "EXISTING---" + rd.title == null ? "NULL" : rd.title);
            rd.save();
        }
    }

    public static void addToRecentViewed(Deal deal) {
        if(deal != null && deal.id > 0) {
            RecentItem rd = new RecentItem();
            List<RecentItem> recentItems = new Select().all().from(RecentItem.class).where("itemId=" + deal.id).execute();
            if(recentItems != null && recentItems.size() > 0) {
                rd = recentItems.get(0);
            }
            rd.id = deal.id;
            rd.type = deal.type;
            rd.description = deal.description;
            rd.city = deal.city;
            rd.title = deal.title;
            rd.views = deal.views;
            rd.rating = deal.rating;
            rd.category = deal.category;
            rd.discount = deal.discount;
            Log.i("UPDATE", "EXISTING---" + rd.title == null ? "NULL" : rd.title);
            rd.save();
        }
    }

    public static void addToRecentViewed(GenericDeal deal) {
        if(deal != null && deal.id > 0) {
            RecentItem recentItem = new RecentItem();
            List<RecentItem> recentItems = new Select().all().from(RecentItem.class).where("itemId=" + deal.id).execute();
            if(recentItems != null && recentItems.size() > 0) {
                recentItem = recentItems.get(0);
            }
            recentItem.id = deal.id;
            recentItem.title = deal.title;
            recentItem.views = deal.views;
            recentItem.rating = deal.rating;
            recentItem.discount = deal.discount;
            recentItem.category = deal.category;
            recentItem.description = deal.description;
            Log.i("UPDATE", "EXISTING---" + recentItem.title == null ? "NULL" : recentItem.title);
            recentItem.save();
        }
    }
}
