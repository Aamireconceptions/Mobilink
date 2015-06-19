package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.TopBrandsPagerAdapter;
import com.ooredoo.bizstore.adapters.TopDealsPagerAdapter;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.PageIndicator;
import com.ooredoo.bizstore.views.HeaderGridView;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment {
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        init(v);

        return v;
    }

    private void init(View v) {
        activity = getActivity();

        HeaderGridView headerGridView = (HeaderGridView) v.findViewById(R.id.home_grid_view);

        LayoutInflater inflater = activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_fragment_home_gridview_header, null);

        TopBrandsPagerAdapter brandsAdapter = new TopBrandsPagerAdapter(getChildFragmentManager());
        TopDealsPagerAdapter promoDealsAdapter = new TopDealsPagerAdapter(getChildFragmentManager());
        TopDealsPagerAdapter featuredDealsAdapter = new TopDealsPagerAdapter(getChildFragmentManager());

        setupViewPager(header, R.id.vp_top_brands, 0, brandsAdapter);
        setupViewPager(header, R.id.vp_promo_deals, R.id.indicator_promo_deals, promoDealsAdapter);
        setupViewPager(header, R.id.vp_featured_deals, R.id.indicator_featured_deals, featuredDealsAdapter);

        headerGridView.addHeaderView(header);
        headerGridView.setAdapter(null);
    }

    private static void setupViewPager(View parent, int pagerId, int indicatorId, FragmentPagerAdapter adapter) {
        ViewPager viewPager = (ViewPager) parent.findViewById(pagerId);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0, true);
        if(indicatorId > 0) {
            PageIndicator pageIndicator = (CirclePageIndicator) parent.findViewById(indicatorId);
            pageIndicator.setViewPager(viewPager);
        }
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

}