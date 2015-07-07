package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.FeaturedStatePagerAdapter;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.PromoStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.FeaturedTask;
import com.ooredoo.bizstore.asynctasks.PromoTask;
import com.ooredoo.bizstore.asynctasks.TopBrandsTask;
import com.ooredoo.bizstore.asynctasks.TopMallsTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.listeners.DashboardItemClickListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment implements OnFilterChangeListener
{
    private HomeActivity activity;

    private TextView tvDealsOfTheDay;

    private ListViewBaseAdapter adapter;

    DashboardItemClickListener dashboardItemClickListener;

    public static HomeFragment newInstance()
    {
        HomeFragment fragment = new HomeFragment();

        return fragment;
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
        activity = (HomeActivity) getActivity();
        activity.setCurrentFragment(this);

        ListView listView = (ListView) v.findViewById(R.id.home_list_view);

        LayoutInflater inflater = activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_fragment_home_listview_header, null);

        setDashboardItemsClickListener(header);

        tvDealsOfTheDay = (TextView) header.findViewById(R.id.deals_of_day);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);

        listView.addHeaderView(header);
        listView.setAdapter(adapter);

        initAndLoadFeaturedDeals(v);

        initAndLoadPromotions(v);

        initAndLoadTopBrands(v);

        //initAndLoadTopMalls(v);

        initAndLoadDealsOfTheDay();
    }

    private void setDashboardItemsClickListener(View parent) {
        dashboardItemClickListener = new DashboardItemClickListener(activity);

        parent.findViewById(R.id.entertainment).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.shopping).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.fashion).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.restaurants).setOnClickListener(dashboardItemClickListener);

        parent.findViewById(R.id.weight_loss).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.fitness).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.beauty_tips).setOnClickListener(dashboardItemClickListener);

        parent.findViewById(R.id.jokes).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.events).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.movie_tickets).setOnClickListener(dashboardItemClickListener);
    }

    private void initAndLoadFeaturedDeals(View v)
    {
        List<GenericDeal> deals = new ArrayList<>();

        FeaturedStatePagerAdapter adapter = new FeaturedStatePagerAdapter(getFragmentManager(), deals);

        ViewPager featuredPager = (ViewPager) v.findViewById(R.id.featured_pager);
        featuredPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.featured_indicator);
        circlePageIndicator.setViewPager(featuredPager);

        FeaturedTask featuredTask = new FeaturedTask(adapter, featuredPager);
        featuredTask.execute();
    }

    private void initAndLoadPromotions(View v)
    {
        List<GenericDeal> deals = new ArrayList<>();

        PromoStatePagerAdapter adapter = new PromoStatePagerAdapter(getFragmentManager(), deals);

        ViewPager promoPager = (ViewPager) v.findViewById(R.id.promo_pager);
        promoPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)
                                                   v.findViewById(R.id.promo_indicator);
        circlePageIndicator.setViewPager(promoPager);

        PromoTask promoTask = new PromoTask(adapter, promoPager);
        promoTask.execute();
    }

    private void initAndLoadTopBrands(View v)
    {
        List<Brand> brands = new ArrayList<>();

        TopBrandsStatePagerAdapter adapter = new TopBrandsStatePagerAdapter(getFragmentManager(),
                                                                            brands);

        ViewPager topBrandsPager = (ViewPager) v.findViewById(R.id.top_brands_pager);
        topBrandsPager.setAdapter(adapter);

        TopBrandsTask topBrandsTask = new TopBrandsTask(adapter, topBrandsPager);
        topBrandsTask.execute("malls");
    }

    private void initAndLoadTopMalls(View v)
    {
        List<Mall> malls = new ArrayList<>();

        TopMallsStatePagerAdapter adapter = new TopMallsStatePagerAdapter(getFragmentManager(),
                                                                          malls);

        ViewPager topMallsPager = (ViewPager) v.findViewById(R.id.top_malls_pager);
        topMallsPager.setAdapter(adapter);

        TopMallsTask topMallsTask = new TopMallsTask(adapter, topMallsPager);

        adapter.notifyDataSetChanged();
        topMallsTask.execute();
    }

    private void initAndLoadDealsOfTheDay()
    {
        DealsTask dealsTask = new DealsTask(activity, adapter, null);
        dealsTask.setTvDealsOfTheDay(tvDealsOfTheDay);
        dealsTask.execute("deals_of_the_day");
    }

    @Override
    public void onFilterChange()
    {
        initAndLoadDealsOfTheDay();
    }
}