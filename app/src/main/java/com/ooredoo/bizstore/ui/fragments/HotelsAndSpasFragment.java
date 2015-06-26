package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class HotelsAndSpasFragment extends Fragment
{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    public static HotelsAndSpasFragment newInstance()
    {
        HotelsAndSpasFragment fragment = new HotelsAndSpasFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_food_dinning, container, false);

        init(v, inflater);

        fetchAndDisplayHotelsAndSpas();

        return v;
    }

    private void init(View v, LayoutInflater inflater)
    {
        activity = (HomeActivity) getActivity();

        View header = inflater.inflate(R.layout.header_deals, null);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity);

        Button btNewDeals = (Button) header.findViewById(R.id.btn_new_deals);
        btNewDeals.setOnClickListener(clickListener);

        Button btPopularDeals = (Button) header.findViewById(R.id.btn_popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        ImageView ivFilter = (ImageView) header.findViewById(R.id.iv_filter);
        ivFilter.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);

        ListView listView = (ListView) v.findViewById(R.id.lv);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);
    }

    private void fetchAndDisplayHotelsAndSpas()
    {
        DealsTask dealsTask = new DealsTask(adapter);
        dealsTask.execute("hotels_spas");
    }

    /*private void init(View v) {
        List<Deal> deals = new ArrayList<>();
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        ListView listView = (ListView) v.findViewById(R.id.lv);
        TopDealsAdapter adapter = new TopDealsAdapter(mActivity, R.layout.list_item_food_n_dining, deals);
        listView.setAdapter(adapter);
    }*/
}