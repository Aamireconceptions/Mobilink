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
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.TopDealsBannersTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnRefreshListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.listeners.ScrollListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.PageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.CategoryUtils.CT_TOP;
import static com.ooredoo.bizstore.utils.CategoryUtils.showSubCategories;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

public class TopDealsFragment extends Fragment implements OnFilterChangeListener,
                                                          OnRefreshListener,
                                                          OnDealsTaskFinishedListener

{
    private HomeActivity mActivity;

    private View headerViewPager, headerFilter;

    private ViewPager viewPager;

    private ListViewBaseAdapter listAdapter;

    public static String subCategory = "";

    private View mView;

    public static TopDealsFragment newInstance() {
        TopDealsFragment fragment = new TopDealsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_top_deals, container, false);

        mView = v;

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

        FontUtils.setFont(mActivity, BizStore.DEFAULT_FONT, btNewDeals);

        Button btPopularDeals = (Button) headerFilter.findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        FontUtils.setFont(mActivity, BizStore.DEFAULT_FONT, btPopularDeals);

        ImageView ivFilter = (ImageView) headerFilter.findViewById(R.id.filter);
        ivFilter.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        listAdapter = new ListViewBaseAdapter(mActivity, R.layout.list_deal, deals);
        listAdapter.setCategory(ResourceUtils.AUTOMOTIVE);

        TextView tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        ListView listView = (ListView) v.findViewById(R.id.list_view);

        listView.setOnScrollListener(new ScrollListener(mActivity));

        listView.addHeaderView(headerViewPager);
        listView.addHeaderView(headerFilter);
        listView.setEmptyView(tvEmptyView);
        listView.setAdapter(listAdapter);

        initAndLoadTopDealsBanner();

        loadTopDeals();
    }


    TopDealsPagerAdapter topDealsadapter;
    private void initAndLoadTopDealsBanner()
    {
        List<GenericDeal> deals = new ArrayList<>();

        topDealsadapter = new TopDealsPagerAdapter(getFragmentManager(), deals);

        viewPager = (ViewPager) headerViewPager.findViewById(R.id.view_pager);
        viewPager.setAdapter(topDealsadapter);

        PageIndicator pageIndicator = (CirclePageIndicator) headerViewPager.findViewById(R.id.pager_indicator);
        pageIndicator.setViewPager(viewPager);

        loadTopDealBanners();
    }

    private void loadTopDealBanners()
    {
        TopDealsBannersTask topDealsBannersTask = new TopDealsBannersTask(mActivity, topDealsadapter,
                                                                          viewPager);
        topDealsBannersTask.execute();
    }

    private void loadTopDeals()
    {
        DealsTask dealsTask = new DealsTask(mActivity, listAdapter, null, null, this);

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

    @Override
    public void onRefreshStarted()
    {
        loadTopDealBanners();

        loadTopDeals();
    }

    @Override
    public void onHaveDeals()
    {
        headerFilter.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals() {

        headerFilter.setVisibility(View.GONE);

    }
}