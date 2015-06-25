package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.FeaturedStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.PromoStatePagerAdapter;
import com.ooredoo.bizstore.asynctasks.FeaturedAsyncTask;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment
{
    private Activity activity;

    public static HomeFragment newInstance()
    {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        init(v);

        return v;
    }

    private void init(View v)
    {
        activity = getActivity();

        ListView listView = (ListView) v.findViewById(R.id.home_list_view);

        LayoutInflater inflater = activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_fragment_home_listview_header, null);

        ListViewBaseAdapter adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, null);

        listView.addHeaderView(header);
        listView.setAdapter(adapter);

        initAndLoadFeaturedDeals(v);

        initAndLoadPromotions(v);

        ViewPager topBrandsPager = (ViewPager) v.findViewById(R.id.top_brands_pager);
        topBrandsPager.setAdapter(new TopBrandsStatePagerAdapter(getFragmentManager()));
    }

    private void initAndLoadFeaturedDeals(View v)
    {
        List<GenericDeal> deals = new ArrayList<>();

        FeaturedStatePagerAdapter adapter = new FeaturedStatePagerAdapter(getFragmentManager(), deals);

        ViewPager featuredPager = (ViewPager) v.findViewById(R.id.featured_pager);
        featuredPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.featured_indicator);
        circlePageIndicator.setViewPager(featuredPager);

        FeaturedAsyncTask featuredAsyncTask = new FeaturedAsyncTask(adapter, featuredPager);
        featuredAsyncTask.execute();
    }

    private void initAndLoadPromotions(View v)
    {
        List<GenericDeal> deals = new ArrayList<>();

        FeaturedStatePagerAdapter adapter = new FeaturedStatePagerAdapter(getFragmentManager(), deals);

        ViewPager promoPager = (ViewPager) v.findViewById(R.id.promo_pager);
        promoPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.promo_indicator);
        circlePageIndicator.setViewPager(promoPager);

        FeaturedAsyncTask featuredAsyncTask = new FeaturedAsyncTask(adapter, promoPager);
        featuredAsyncTask.execute();
    }
}