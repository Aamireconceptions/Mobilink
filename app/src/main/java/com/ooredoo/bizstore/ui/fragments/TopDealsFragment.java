package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.DealsAdapter;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.FeaturedTask;
import com.ooredoo.bizstore.asynctasks.TopDealsBannersTask;
import com.ooredoo.bizstore.listeners.DealsFilterClickListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.PageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class TopDealsFragment extends Fragment
{
    private HomeActivity mActivity;

    private View headerViewPager, headerFilter;

    private ListViewBaseAdapter adapter;

    public static TopDealsFragment newInstance() {
        TopDealsFragment fragment = new TopDealsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_top_deals, container, false);

        init(v, inflater);

        return v;
    }

    private void init(View v, LayoutInflater inflater)
    {
        mActivity = (HomeActivity) getActivity();

        headerViewPager = inflater.inflate(R.layout.viewpager, null);
        headerFilter = inflater.inflate(R.layout.header_deals, null);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(mActivity, R.layout.list_deal, deals);

        ListView listView = (ListView) v.findViewById(R.id.lv);
        listView.addHeaderView(headerViewPager);
        listView.addHeaderView(headerFilter);
        listView.setAdapter(adapter);

        initAndLoadTopDealsBanner();

        loadTopDeals();
    }

    private void initAndLoadTopDealsBanner()
    {
        List<GenericDeal> deals = new ArrayList<>();

        TopDealsPagerAdapter adapter = new TopDealsPagerAdapter(getFragmentManager(), deals);

        ViewPager viewPager = (ViewPager) headerViewPager.findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        PageIndicator pageIndicator = (CirclePageIndicator) headerViewPager.findViewById(R.id.pager_indicator);
        pageIndicator.setViewPager(viewPager);

        TopDealsBannersTask topDealsBannersTask = new TopDealsBannersTask(adapter, viewPager);
        topDealsBannersTask.execute();
    }

    private void loadTopDeals()
    {
        DealsTask dealsTask = new DealsTask(adapter, null);
        dealsTask.execute("top_deals");
    }
}