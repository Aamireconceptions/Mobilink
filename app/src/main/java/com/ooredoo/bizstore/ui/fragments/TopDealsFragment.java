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
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.listeners.DealsFilterClickListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.PageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class TopDealsFragment extends Fragment {
    HomeActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top_deals, container, false);

        mActivity = (HomeActivity) getActivity();

        View header_filters = inflater.inflate(R.layout.header_deals, null);
        View header_view_pager = inflater.inflate(R.layout.viewpager, null);

        new DealsFilterClickListener(mActivity, header_filters);

        ListView listView = (ListView) v.findViewById(R.id.lv);

        ViewPager viewPager = (ViewPager) header_view_pager.findViewById(R.id.view_pager);
        PageIndicator pageIndicator = (CirclePageIndicator) header_view_pager.findViewById(R.id.pager_indicator);

        List<GenericDeal> deals = new ArrayList<>();

        TopDealsPagerAdapter pagerAdapter = new TopDealsPagerAdapter(getChildFragmentManager(), deals);

        viewPager.setAdapter(pagerAdapter);

        pageIndicator.setViewPager(viewPager);

        viewPager.setCurrentItem(0, true);
        listView.addHeaderView(header_view_pager);
        listView.addHeaderView(header_filters);

        init(v);

        return v;
    }

    private void init(View v) {
        populateDeals(v);
    }

    private void populateDeals(View v) {
        List<Deal> deals = new ArrayList<>();
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        ListView listView = (ListView) v.findViewById(R.id.lv);
        DealsAdapter adapter = new DealsAdapter(mActivity, R.layout.list_item_deal, deals);
        listView.setAdapter(adapter);
    }

    public static TopDealsFragment newInstance() {
        TopDealsFragment fragment = new TopDealsFragment();
        return fragment;
    }
}