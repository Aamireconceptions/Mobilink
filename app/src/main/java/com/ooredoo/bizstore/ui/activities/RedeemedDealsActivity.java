package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.RedeemedDealsBaseAdapter;
import com.ooredoo.bizstore.asynctasks.GetRedeemedDealsTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Babar on 07-Aug-15.
 */
public class RedeemedDealsActivity extends AppCompatActivity implements OnDealsTaskFinishedListener,
                                                                View.OnClickListener,
                                                                SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;

    private TextView tvEmptyView;

    private List<GenericDeal> deals;

    private RedeemedDealsBaseAdapter adapter;

    private ProgressBar progressBar;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    private View lastSelected;

    private String type = "Available";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_redeemed_deals);

        setupToolbar();

        init();
    }

    private void setupToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void init()
    {
        Button btAvailable = (Button) findViewById(R.id.available);
        btAvailable.setOnClickListener(this);
        setSelected(btAvailable);

        Button btRedeemed = (Button) findViewById(R.id.redeemed);
        btRedeemed.setOnClickListener(this);

        tvEmptyView = (TextView) findViewById(R.id.empty_view);

        listView = (ListView) findViewById(R.id.list_view);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        deals = new ArrayList<>();

        adapter = new RedeemedDealsBaseAdapter(this, R.layout.list_redeemed_deal, deals);

        listView.setAdapter(adapter);

        /*swipeRefreshLayout = (MultiSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.list_view, R.id.empty_view, R.id.progressBar);
        swipeRefreshLayout.setOnRefreshListener(this);*/

        getRedeemedDeals();
    }

    private void getRedeemedDeals()
    {
        GetRedeemedDealsTask getRedeemedDealsTask = new GetRedeemedDealsTask(this, adapter,
                                                                             progressBar, deals);
        getRedeemedDealsTask.execute(type);
    }

    @Override
    public void onRefresh()
    {
        tvEmptyView.setText("");

        clearData();

        getRedeemedDeals();
    }

    @Override
    public void onRefreshCompleted() {
        //swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {

    }

    @Override
    public void onNoDeals(int stringResId) {

        tvEmptyView.setText(stringResId);

        listView.setEmptyView(tvEmptyView);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.available:

                if(!v.isSelected())
                {
                    type = "Available";

                    setSelected(v);

                    onRefresh();
                }
                else
                {
                    Logger.print("Available Already Selected");
                }

                break;

            case R.id.redeemed:

                if(!v.isSelected())
                {
                    type = "Redeemed";

                    setSelected(v);

                    onRefresh();
                }
                else
                {
                    Logger.print("Redeemed Already Selected");
                }

                break;
        }
    }

    private void setSelected(View v)
    {
        v.setSelected(!v.isSelected());

        if(lastSelected != null)
        {
            lastSelected.setSelected(false);
        }

        lastSelected = v;
    }

    private void clearData()
    {
        deals.clear();

        adapter.notifyDataSetChanged();
    }
}