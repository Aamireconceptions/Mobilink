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
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class SportsAndFitnessFragment extends Fragment
{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    public static SportsAndFitnessFragment newInstance()
    {
        SportsAndFitnessFragment fragment = new SportsAndFitnessFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_list_view, container, false);

        init(v, inflater);

        fetchAndDisplaySportsAndFitness();

        return v;
    }

    private void init(View v, LayoutInflater inflater)
    {
        activity = (HomeActivity) getActivity();

        View header = inflater.inflate(R.layout.header_deals, null);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity);

        Button btNewDeals = (Button) header.findViewById(R.id.btn_new_deals);
        btNewDeals.setOnClickListener(clickListener);
        btNewDeals.setSelected(true);

        Button btPopularDeals = (Button) header.findViewById(R.id.btn_popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        ImageView ivFilter = (ImageView) header.findViewById(R.id.iv_filter);
        ivFilter.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);

        ListView listView = (ListView) v.findViewById(R.id.list_view);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void fetchAndDisplaySportsAndFitness()
    {
        DealsTask dealsTask = new DealsTask(adapter, progressBar);
        dealsTask.execute("sports_fitness");
    }
}