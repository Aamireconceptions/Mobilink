package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
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
import com.ooredoo.bizstore.interfaces.OnRefreshListener;
import com.ooredoo.bizstore.listeners.DashboardItemClickListener;
import com.ooredoo.bizstore.listeners.ScrollListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.MyScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment implements OnFilterChangeListener, OnRefreshListener {
    private HomeActivity activity;

    private TextView tvDealsOfTheDay;

    private ListViewBaseAdapter listAdapter;

    DashboardItemClickListener dashboardItemClickListener;

    public static ViewPager featuredPager, promoPager, topBrandsPager, topMallsPager;

    boolean sliderOn = true;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        init(v);

        return v;
    }

    private void init(View v) {
        activity = (HomeActivity) getActivity();
        activity.setCurrentFragment(this);

        ListView listView = (ListView) v.findViewById(R.id.home_list_view);
        listView.setOnScrollListener(new ScrollListener(activity));

        LayoutInflater inflater = activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_fragment_home_listview_header, null);

        setDashboardItemsClickListener(header);

        TextView tvTopDeals = (TextView) header.findViewById(R.id.top_deals);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvTopDeals);

        TextView tvTopBrands = (TextView) header.findViewById(R.id.top_brands);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvTopBrands);

        TextView tvHealthFitness = (TextView) header.findViewById(R.id.health_fitness);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvHealthFitness);

        TextView tvTopMalls = (TextView) header.findViewById(R.id.top_malls);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvTopMalls);

        TextView tvEntertainment = (TextView) header.findViewById(R.id.entertainment_header);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvEntertainment);

        tvDealsOfTheDay = (TextView) header.findViewById(R.id.deals_of_day);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvDealsOfTheDay);

        List<GenericDeal> deals = new ArrayList<>();

        listAdapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);

        listView.addHeaderView(header);
        listView.setAdapter(listAdapter);

        initAndLoadPromotions(v);

        initAndLoadFeaturedDeals(v);

        initAndLoadTopBrands(v);

        initAndLoadTopMalls(v);

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

    PromoStatePagerAdapter promoAdapter;
    private void initAndLoadPromotions(View v) {
        List<GenericDeal> deals = new ArrayList<>();

        promoAdapter = new PromoStatePagerAdapter(getFragmentManager(), deals);

        promoPager = (ViewPager) v.findViewById(R.id.promo_pager);
        promoPager.setAdapter(promoAdapter);

        setupScroller(promoPager);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.promo_indicator);
        circlePageIndicator.setViewPager(promoPager);

        loadPromos();
    }

    private void loadPromos()
    {
        PromoTask promoTask = new PromoTask(activity, promoAdapter, promoPager);
        promoTask.execute();
    }

    FeaturedStatePagerAdapter featuredAdapter;
    private void initAndLoadFeaturedDeals(View v) {
        List<GenericDeal> deals = new ArrayList<>();

        featuredAdapter = new FeaturedStatePagerAdapter(getFragmentManager(), deals);

        featuredPager = (ViewPager) v.findViewById(R.id.featured_pager);
        featuredPager.setAdapter(featuredAdapter);

        setupScroller(featuredPager);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) v.findViewById(R.id.featured_indicator);
        circlePageIndicator.setViewPager(featuredPager);

        loadFeatured();
    }

    private void loadFeatured()
    {
        FeaturedTask featuredTask = new FeaturedTask(activity, featuredAdapter, featuredPager);
        featuredTask.execute();
    }

    TopBrandsStatePagerAdapter topBrandsStatePagerAdapter;
    private void initAndLoadTopBrands(View v) {
        List<Brand> brands = new ArrayList<>();

        topBrandsStatePagerAdapter = new TopBrandsStatePagerAdapter(getFragmentManager(), brands);

        topBrandsPager = (ViewPager) v.findViewById(R.id.top_brands_pager);
        topBrandsPager.setAdapter(topBrandsStatePagerAdapter);

        loadTopBrands();
    }

    private void loadTopBrands()
    {
        TopBrandsTask topBrandsTask = new TopBrandsTask(topBrandsStatePagerAdapter, topBrandsPager);
        topBrandsTask.execute("malls");
    }

    TopMallsStatePagerAdapter topMallsAdapter;
    private void initAndLoadTopMalls(View v) {
        List<Mall> malls = new ArrayList<>();

        topMallsAdapter = new TopMallsStatePagerAdapter(getFragmentManager(), malls);

        topMallsPager = (ViewPager) v.findViewById(R.id.top_malls_pager);
        topMallsPager.setAdapter(topMallsAdapter);

        loadTopMalls();
    }

    private void loadTopMalls()
    {
        TopMallsTask topMallsTask = new TopMallsTask(topMallsAdapter, topMallsPager);
        topMallsTask.execute("malls");
    }

    private void initAndLoadDealsOfTheDay() {
        DealsTask dealsTask = new DealsTask(activity, listAdapter, null);
        dealsTask.setTvDealsOfTheDay(tvDealsOfTheDay);
        dealsTask.execute("dealofday");
    }

    private void setupScroller(ViewPager viewPager) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(viewPager, new MyScroller(activity));
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFilterChange() {
        initAndLoadDealsOfTheDay();
    }

    @Override
    public void onRefreshStarted()
    {
        loadPromos();
        loadFeatured();
        loadTopBrands();
        loadTopMalls();
        initAndLoadDealsOfTheDay();
    }

    public static void startSlider(final List list, final ViewPager viewPager) {

        Timer timer;

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {

            int position = 0;

            boolean fwd = true;

            @Override
            public void run() {
                if(list != null && list.size() > 0) {
                    if(position < list.size() - 1 || !fwd) {
                        if(position == 0) {
                            fwd = true;
                        }
                    } else {
                        fwd = false;
                    }

                    if(fwd)
                        position++;
                    else
                        position--;

                    viewPager.setCurrentItem(position, true);
                    synchronized(viewPager) {
                        viewPager.notifyAll();
                    }
                }
            }
        };

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 3000, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderOn = true;
        startSlider(PromoStatePagerAdapter.deals, promoPager);
        startSlider(FeaturedStatePagerAdapter.deals, featuredPager);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderOn = false;
    }


}