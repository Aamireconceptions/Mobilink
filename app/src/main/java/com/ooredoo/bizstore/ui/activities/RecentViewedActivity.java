package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.RecentDealsAdapter;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.RecentDeal;

import java.util.List;

public class RecentViewedActivity extends AppCompatActivity implements View.OnClickListener {
    private View lastSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_deals);

        init();
    }

    private void init() {
        setupToolbar();

        Button btnNewDeals = (Button) findViewById(R.id.new_deals);
        Button btnPopularDeals = (Button) findViewById(R.id.popular_deals);

        btnNewDeals.setSelected(true);
        btnNewDeals.setOnClickListener(this);
        btnPopularDeals.setOnClickListener(this);

        lastSelected = btnNewDeals;

        List<RecentDeal> deals = new Select().all().from(RecentDeal.class).execute();

        ListView listView = (ListView) findViewById(R.id.list_view);

        RecentDealsAdapter adapter = new RecentDealsAdapter(this, R.layout.list_item_deal, deals);

        listView.setAdapter(adapter);
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
        }

    }

    private void setSelected(View v) {
        if(lastSelected != null) {
            lastSelected.setSelected(false);
        }

        v.setSelected(true);

        lastSelected = v;
    }

    public static void addToRecentViewed(Deal deal) {
        if(deal != null && deal.id > 0) {
            RecentDeal rd = new RecentDeal();
            List<RecentDeal> deals = new Select().all().from(RecentDeal.class).where("dealId=" + deal.id).execute();
            if(deals != null && deals.size() > 0) {
                rd = deals.get(0);
            }
            rd.id = deal.id;
            rd.type = deal.type;
            rd.desc = deal.desc;
            rd.city = deal.city;
            rd.title = deal.title;
            rd.discount = deal.discount;
            Log.i("UPDATE", "EXISTING---" + rd.title);
            rd.save();
        }
    }
}
