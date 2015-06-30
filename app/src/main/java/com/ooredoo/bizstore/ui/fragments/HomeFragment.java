package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.FeaturedStatePagerAdapter;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.FeaturedTask;
import com.ooredoo.bizstore.asynctasks.PromoTask;
import com.ooredoo.bizstore.asynctasks.TopBrandsTask;
import com.ooredoo.bizstore.asynctasks.TopMallsTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.CirclePageIndicator;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment
{
    private Activity activity;

    private TextView tvDealsOfTheDay;

    private ListViewBaseAdapter adapter;

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
        activity = getActivity();

        ListView listView = (ListView) v.findViewById(R.id.home_list_view);

        LayoutInflater inflater = activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_fragment_home_listview_header, null);

        tvDealsOfTheDay = (TextView) header.findViewById(R.id.deals_of_day);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);

        listView.addHeaderView(header);
        listView.setAdapter(adapter);

        initAndLoadFeaturedDeals(v);

        initAndLoadPromotions(v);

        initAndLoadTopBrands(v);

        initAndLoadTopMalls(v);

        initAndLoadDealsOfTheDay();
    }

    private void initAndLoadFeaturedDeals(View v)
    {
        List<GenericDeal> deals = new ArrayList<>();
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());

        FeaturedStatePagerAdapter adapter = new FeaturedStatePagerAdapter(getFragmentManager(), deals);

        ViewPager featuredPager = (ViewPager) v.findViewById(R.id.featured_pager);
        featuredPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.featured_indicator);
        circlePageIndicator.setViewPager(featuredPager);

        FeaturedTask featuredTask = new FeaturedTask(adapter, featuredPager);
        //featuredTask.execute();
    }

    private void initAndLoadPromotions(View v)
    {
        List<GenericDeal> deals = new ArrayList<>();
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());
        deals.add(new GenericDeal());

        FeaturedStatePagerAdapter adapter = new FeaturedStatePagerAdapter(getFragmentManager(), deals);

        ViewPager promoPager = (ViewPager) v.findViewById(R.id.promo_pager);
        promoPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)
                                                   v.findViewById(R.id.promo_indicator);
        circlePageIndicator.setViewPager(promoPager);

        PromoTask promoTask = new PromoTask(adapter, promoPager);
        //promoTask.execute();
    }

    private void initAndLoadTopBrands(View v)
    {
        List<Brand> brands = new ArrayList<>();
        brands.add(new Brand());
        brands.add(new Brand());
        brands.add(new Brand());
        brands.add(new Brand());
        brands.add(new Brand());

        TopBrandsStatePagerAdapter adapter = new TopBrandsStatePagerAdapter(getFragmentManager(),
                                                                            brands);

        ViewPager topBrandsPager = (ViewPager) v.findViewById(R.id.top_brands_pager);
        topBrandsPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)
                                                   v.findViewById(R.id.promo_indicator);
        circlePageIndicator.setViewPager(topBrandsPager);

        TopBrandsTask topBrandsTask = new TopBrandsTask(adapter, topBrandsPager);
       // topBrandsTask.execute();
    }

    private void initAndLoadTopMalls(View v)
    {
        List<Mall> malls = new ArrayList<>();
        malls.add(new Mall());
        malls.add(new Mall());
        malls.add(new Mall());
        malls.add(new Mall());
        malls.add(new Mall());

        TopMallsStatePagerAdapter adapter = new TopMallsStatePagerAdapter(getFragmentManager(),
                                                                          malls);

        ViewPager topMallsPager = (ViewPager) v.findViewById(R.id.top_malls_pager);

        topMallsPager.setAdapter(adapter);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)
                                                   v.findViewById(R.id.promo_indicator);
        circlePageIndicator.setViewPager(topMallsPager);

        TopMallsTask topMallsTask = new TopMallsTask(adapter, topMallsPager);

        adapter.notifyDataSetChanged();
        //topMallsTask.execute();
    }

    private void initAndLoadDealsOfTheDay()
    {
        DealsTask dealsTask = new DealsTask(adapter, null);
        dealsTask.setTvDealsOfTheDay(tvDealsOfTheDay);
        dealsTask.execute("deals_of_the_day");
    }
}