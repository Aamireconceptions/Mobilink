package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.listeners.ListViewOnItemClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class ElectronicsFragment extends Fragment
{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    public static ElectronicsFragment newInstance()
    {
        ElectronicsFragment fragment = new ElectronicsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v);

        fetchAndDisplayElectronics();

        return v;
    }

    private void init(View v)
    {
        activity = (HomeActivity) getActivity();

        FilterOnClickListener clickListener = new FilterOnClickListener(activity);

        Button btNewDeals = (Button) v.findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(clickListener);
        clickListener.setButtonSelected(btNewDeals);

        Button btPopularDeals = (Button) v.findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        ImageView ivFilter = (ImageView) v.findViewById(R.id.filter);
        ivFilter.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);

        ListView listView = (ListView) v.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void fetchAndDisplayElectronics()
    {
        DealsTask dealsTask = new DealsTask(adapter, progressBar);
        dealsTask.execute("electronics");
    }
}