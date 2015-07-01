package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LayoutDirection;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.RecentDeal;
import com.ooredoo.bizstore.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class RecentViewedActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_deals);

        init();
    }

    private void init()
    {
        setupToolbar();

        FilterOnClickListener clickListener = new FilterOnClickListener(this);

        Button btNewDeals = (Button) findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(clickListener);
        clickListener.setButtonSelected(btNewDeals);

        Button btPopularDeals = (Button) findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        ListViewBaseAdapter adapter = new ListViewBaseAdapter(this, R.layout.list_deal, deals);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
            Logger.logI("UPDATE", "EXISTING---" + rd.title);
            rd.save();
        }
    }
}
