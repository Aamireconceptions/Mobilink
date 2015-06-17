package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.TopDealsAdapter;
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.PageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class TopDealsFragment extends Fragment implements View.OnClickListener {
    HomeActivity mActivity;

    Button btnNewDeals, btnPopularDeals;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top_deals, container, false);

        mActivity = (HomeActivity) getActivity();

        View header_filters = inflater.inflate(R.layout.header_deals, null);
        View header_view_pager = inflater.inflate(R.layout.viewpager, null);

        ListView listView = (ListView) v.findViewById(R.id.lv);

        ViewPager viewPager = (ViewPager) header_view_pager.findViewById(R.id.view_pager);
        PageIndicator pageIndicator = (CirclePageIndicator) header_view_pager.findViewById(R.id.pager_indicator);

        TopDealsPagerAdapter pagerAdapter = new TopDealsPagerAdapter(mActivity);

        viewPager.setAdapter(pagerAdapter);

        pageIndicator.setViewPager(viewPager);

        btnNewDeals = (Button) header_filters.findViewById(R.id.btn_new_deals);
        btnPopularDeals = (Button) header_filters.findViewById(R.id.btn_popular_deals);

        btnNewDeals.setOnClickListener(this);
        btnPopularDeals.setOnClickListener(this);

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
        TopDealsAdapter adapter = new TopDealsAdapter(mActivity, R.layout.list_item_top_deal, deals);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_new_deals || id == R.id.btn_popular_deals) {
            btnNewDeals.setBackgroundResource(id == R.id.btn_new_deals ? R.drawable.btn_lt_grey1 : R.drawable.btn_red1);
            btnPopularDeals.setBackgroundResource(id == R.id.btn_new_deals ? R.drawable.btn_red2 : R.drawable.btn_lt_grey2);
        }
    }

    public static TopDealsFragment newInstance() {
        TopDealsFragment fragment = new TopDealsFragment();
        return fragment;
    }
}