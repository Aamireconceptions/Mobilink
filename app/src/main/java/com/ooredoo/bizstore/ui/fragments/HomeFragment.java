package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.listeners.DashboardItemClickListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.MyScroller;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.utils.SliderUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment implements OnFilterChangeListener,
                                                      OnDealsTaskFinishedListener,
                                                      SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private TextView tvDealsOfTheDay;

    private ListViewBaseAdapter listAdapter;

    DashboardItemClickListener dashboardItemClickListener;

    public static ViewPager featuredPager, promoPager, topBrandsPager, topMallsPager;

    private CirclePageIndicator promoIndicator, featuredIndicator;

    private SliderUtils promoSlider, featuredSlider;

    private SwipeRefreshLayout swipeRefreshLayout;
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

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setOnRefreshListener(this);

        ListView listView = (ListView) v.findViewById(R.id.home_list_view);

        LayoutInflater inflater = activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_fragment_home_listview_header, null);

        setDashboardItemsClickListener(header);

        TextView tvHotDeals = (TextView) header.findViewById(R.id.hot_deals);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvHotDeals);

        TextView tvTopBrands = (TextView) header.findViewById(R.id.top_brands);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvTopBrands);

        TextView tvHotelsAndSpas = (TextView) header.findViewById(R.id.hotels_spas);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvHotelsAndSpas);

        TextView tvTopMalls = (TextView) header.findViewById(R.id.top_malls);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvTopMalls);

        TextView tvEntertainment = (TextView) header.findViewById(R.id.entertainment_header);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvEntertainment);

        tvDealsOfTheDay = (TextView) header.findViewById(R.id.deals_of_day);
        FontUtils.setFont(activity, BizStore.SERIF_FONT, tvDealsOfTheDay);

        List<GenericDeal> deals = new ArrayList<>();

        listAdapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);
        listAdapter.setCategory(ResourceUtils.TOP_DEALS);

        listView.addHeaderView(header);
        listView.setAdapter(listAdapter);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

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
        parent.findViewById(R.id.electronics).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.restaurants).setOnClickListener(dashboardItemClickListener);

        parent.findViewById(R.id.salons).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.lodging).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.spas).setOnClickListener(dashboardItemClickListener);

        parent.findViewById(R.id.kids_activities).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.events).setOnClickListener(dashboardItemClickListener);
        parent.findViewById(R.id.movie_tickets).setOnClickListener(dashboardItemClickListener);
    }

    PromoStatePagerAdapter promoAdapter;
    private void initAndLoadPromotions(View v) {
        List<GenericDeal> deals = new ArrayList<>();

        promoPager = (ViewPager) v.findViewById(R.id.promo_pager);

        promoSlider = new SliderUtils(promoPager);

        promoAdapter = new PromoStatePagerAdapter(getFragmentManager(), deals, promoSlider);

        promoPager.setAdapter(promoAdapter);

        setupScroller(promoPager);

        promoIndicator = (CirclePageIndicator) v.findViewById(R.id.promo_indicator);
        promoIndicator.setViewPager(promoPager);
        promoIndicator.setVisibility(View.GONE);

        loadPromos();
    }

    private void loadPromos()
    {
        PromoTask promoTask = new PromoTask(activity, promoAdapter,
                                            promoPager, promoIndicator);
        promoTask.execute();
    }

    private FeaturedStatePagerAdapter featuredAdapter;

    private void initAndLoadFeaturedDeals(View v) {
        List<GenericDeal> deals = new ArrayList<>();

        featuredPager = (ViewPager) v.findViewById(R.id.featured_pager);

        featuredSlider = new SliderUtils(featuredPager);

        featuredAdapter = new FeaturedStatePagerAdapter(getFragmentManager(), deals, featuredSlider);

        featuredPager.setAdapter(featuredAdapter);

        setupScroller(featuredPager);

        featuredIndicator = (CirclePageIndicator) v.findViewById(R.id.featured_indicator);
        featuredIndicator.setViewPager(featuredPager);
        featuredIndicator.setVisibility(View.GONE);

        loadFeatured();
    }

    private void loadFeatured()
    {
        final String KEY = "FEATURED_DEALS";

        final String cachedData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, KEY.concat("_UPDATE"));

        if(hasInternetConnection(activity) && (isNullOrEmpty(cachedData) || updateFromServer)) {

            FeaturedTask featuredTask = new FeaturedTask(activity, featuredAdapter, featuredPager, featuredIndicator);
            featuredTask.execute();
        }
    }

    private TopBrandsStatePagerAdapter topBrandsStatePagerAdapter;

    private void initAndLoadTopBrands(View v) {
        List<Brand> brands = new ArrayList<>();

        topBrandsStatePagerAdapter = new TopBrandsStatePagerAdapter(getFragmentManager(), brands);

        topBrandsPager = (ViewPager) v.findViewById(R.id.top_brands_pager);
        topBrandsPager.setAdapter(topBrandsStatePagerAdapter);

        loadTopBrands();
    }

    private void loadTopBrands()
    {
        TopBrandsTask topBrandsTask = new TopBrandsTask(activity, topBrandsStatePagerAdapter, topBrandsPager);
        topBrandsTask.execute("malls");
    }

    private TopMallsStatePagerAdapter topMallsAdapter;

    private void initAndLoadTopMalls(View v) {
        List<Mall> malls = new ArrayList<>();

        topMallsAdapter = new TopMallsStatePagerAdapter(getFragmentManager(), malls);

        topMallsPager = (ViewPager) v.findViewById(R.id.top_malls_pager);
        topMallsPager.setAdapter(topMallsAdapter);

        loadTopMalls();
    }

    private void loadTopMalls()
    {
        TopMallsTask topMallsTask = new TopMallsTask(activity, topMallsAdapter, topMallsPager);
        topMallsTask.execute("malls");
    }

    private void initAndLoadDealsOfTheDay() {
        DealsTask dealsTask = new DealsTask(activity, listAdapter, null, null, this);
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
    public void onResume()
    {
        super.onResume();

        resumeSliders();
    }

    private void resumeSliders()
    {
        int promosCount = promoAdapter.getCount();

        if(promosCount > 0)
        {
            promoSlider.start(promosCount);
        }

        int featuredCount = featuredAdapter.getCount();

        if(featuredCount > 0)
        {
            featuredSlider.start(featuredCount);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        promoSlider.stop();
        featuredSlider.stop();
    }

    @Override
    public void onRefresh() {
        loadPromos();
        loadFeatured();
        loadTopBrands();
        loadTopMalls();
        initAndLoadDealsOfTheDay();
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {
        tvDealsOfTheDay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals(int stringResId) {
        tvDealsOfTheDay.setVisibility(View.GONE);
    }
}