package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.TopDealsBannersTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.PageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.CategoryUtils.CT_TOP;
import static com.ooredoo.bizstore.utils.CategoryUtils.showSubCategories;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

public class TopDealsFragment extends Fragment implements OnFilterChangeListener
{
    private HomeActivity mActivity;

    private View headerViewPager, headerFilter;

    private ListViewBaseAdapter adapter;

    public static String subCategory = "";

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

        showSubCategories(mActivity, CT_TOP);

        headerViewPager = inflater.inflate(R.layout.viewpager, null);
        headerFilter = inflater.inflate(R.layout.header_deals, null);

        FilterOnClickListener clickListener = new FilterOnClickListener(mActivity, CT_TOP);

        Button btNewDeals = (Button) headerFilter.findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(clickListener);
        clickListener.setButtonSelected(btNewDeals);

        Button btPopularDeals = (Button) headerFilter.findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        ImageView ivFilter = (ImageView) headerFilter.findViewById(R.id.filter);
        ivFilter.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(mActivity, R.layout.list_deal, deals);
        adapter.setCategory(ResourceUtils.AUTOMOTIVE);

        ListView listView = (ListView) v.findViewById(R.id.list_view);
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
        DealsTask dealsTask = new DealsTask(mActivity, adapter, null);

        if(isNotNullOrEmpty(subCategory)) {
            DealsTask.subCategories = subCategory;
            subCategory = ""; //Reset sub category filter.
        }

        dealsTask.execute("top_deals");
    }

    @Override
    public void onFilterChange()
    {
        Logger.print("TopDealsFragment onFilterChange");

        loadTopDeals();
    }
}