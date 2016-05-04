package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.PromoStatePagerAdapter;
import com.ooredoo.bizstore.adapters.ViewedRatedAdapter;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.asynctasks.PromoTask;
import com.ooredoo.bizstore.asynctasks.TopBrandsTask;
import com.ooredoo.bizstore.asynctasks.TopMallsTask;
import com.ooredoo.bizstore.asynctasks.ViewedRatedTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.listeners.DashboardItemClickListener;
import com.ooredoo.bizstore.listeners.PromoOnPageChangeListener;
import com.ooredoo.bizstore.listeners.SliderOnTouchListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.DOD;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.MyScroller;
import com.ooredoo.bizstore.utils.SliderUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.clearCache;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment implements OnFilterChangeListener,
                                                      OnDealsTaskFinishedListener,
                                                      SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private TextView tvDealsOfTheDay;

    private ListViewBaseAdapter listAdapter;

    private ViewedRatedAdapter viewedRatedAdapter;

    DashboardItemClickListener dashboardItemClickListener;

    public static ViewPager promoPager, topBrandsPager, topMallsPager;

    private CirclePageIndicator promoIndicator;

    public SliderUtils promoSlider, featuredSlider;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    boolean isRefreshed = false;

    MemoryCache memoryCache = MemoryCache.getInstance();

    DiskCache diskCache = DiskCache.getInstance();

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
dealofDayCalled = false;
        init(v);

        return v;
    }

    boolean dealofDayCalled = false;
    private void init(View v) {
        activity = (HomeActivity) getActivity();

        // This was killing the whole thing!
        //activity.setCurrentFragment(this);

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setSwipeableChildrens(R.id.home_list_view, R.id.empty_view, R.id.appBarLayout);

        final ListView listView = (ListView) v.findViewById(R.id.home_list_view);

        LayoutInflater inflater = activity.getLayoutInflater();

        View header = inflater.inflate(R.layout.layout_dashboard, null);

        TextView tvTopBrands = (TextView) header.findViewById(R.id.top_brands);
        FontUtils.setFont(activity, tvTopBrands);

        String brands = getString(R.string.brands).toUpperCase();
        String ofTheWeek = getString(R.string.off_the_week).toUpperCase();
        String brandsOfTheWeek = brands + " " + ofTheWeek;

        int color = BuildConfig.FLAVOR.equals("ooredoo") || BuildConfig.FLAVOR.equals("mobilink")
                ? R.color.red : R.color.white;


        FontUtils.changeColorAndMakeBold(tvTopBrands, brandsOfTheWeek, brands,
                getResources().getColor(color));

        TextView tvTopMalls = (TextView) header.findViewById(R.id.top_malls);
        FontUtils.setFont(activity, tvTopMalls);

        String top = getString(R.string.top).toUpperCase();
        String malls = getString(R.string.Malls).toUpperCase();
        String topMalls = top + " " + malls;

        FontUtils.changeColorAndMakeBold(tvTopMalls, topMalls, top,
                getResources().getColor(color));



        List<DOD> dods = new ArrayList<>();

        viewedRatedAdapter = new ViewedRatedAdapter(activity, R.layout.layout_deal_of_day, dods);

        listView.addHeaderView(header);
        listView.setAdapter(viewedRatedAdapter);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;


                if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 &&
                        listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                    //It is scrolled all the way down here
                    if (!dealofDayCalled) {
                        dealofDayCalled = true;

                        initAndLoadDealsOfTheDay();

                        Logger.print("Last Item");
                    }
                }
            }
        });

        initAndLoadPromotions(v);

        initAndLoadTopBrands(v);

        initAndLoadTopMalls(v);

       // initAndLoadDealsOfTheDay();
    }

    PromoStatePagerAdapter promoAdapter;
    private void initAndLoadPromotions(View v) {
        List<GenericDeal> deals = new ArrayList<>();

        PromoOnPageChangeListener pageChangeListener = new PromoOnPageChangeListener(swipeRefreshLayout);

        promoPager = (ViewPager) v.findViewById(R.id.promo_pager);
        promoPager.addOnPageChangeListener(pageChangeListener);

        ProgressBar pbPromo = (ProgressBar) v.findViewById(R.id.promo_progress);

        promoSlider = new SliderUtils(activity, promoPager);

        promoPager.setOnTouchListener(new SliderOnTouchListener(promoSlider));

        promoAdapter = new PromoStatePagerAdapter(getFragmentManager(), deals, promoSlider);

        promoPager.setAdapter(promoAdapter);

        setupScroller(promoPager);

        promoIndicator = (CirclePageIndicator) v.findViewById(R.id.promo_indicator);
        promoIndicator.setViewPager(promoPager);
        promoIndicator.setVisibility(View.GONE);
        promoIndicator.setPageColor(getResources().getColor(R.color.indicator_normal));
        promoIndicator.setFillColor(getResources().getColor(R.color.red));

        loadPromos(pbPromo);
    }

    private void loadPromos(ProgressBar pbPromo)
    {
        PromoTask promoTask = new PromoTask(activity, promoAdapter,
                                            promoPager, promoIndicator, pbPromo);

        String cache = promoTask.getCache();

        if(cache != null && !isRefreshed)
        {
            promoTask.setData(cache);
        }
        else
        {
            promoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private TopBrandsStatePagerAdapter topBrandsStatePagerAdapter;

    private void initAndLoadTopBrands(View v) {
        List<Brand> brands = new ArrayList<>();

        topBrandsStatePagerAdapter = new TopBrandsStatePagerAdapter(getFragmentManager(), brands);

        topBrandsPager = (ViewPager) v.findViewById(R.id.top_brands_pager);

        ProgressBar pbTopBrands = (ProgressBar) v.findViewById(R.id.top_brands_progress);

        topBrandsPager.setAdapter(topBrandsStatePagerAdapter);

        loadTopBrands(pbTopBrands);
    }

    private void loadTopBrands(ProgressBar pbTopBrands)
    {
        TopBrandsTask topBrandsTask = new TopBrandsTask(activity, topBrandsStatePagerAdapter,
                                                        topBrandsPager, pbTopBrands);

        String cache = topBrandsTask.getCache();

        if(cache != null && !isRefreshed)
        {
            topBrandsTask.setData(cache);
        }
        else
        {
            topBrandsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "brands");
        }
    }

    private TopMallsStatePagerAdapter topMallsAdapter;

    private void initAndLoadTopMalls(View v) {
        List<Mall> malls = new ArrayList<>();

        topMallsAdapter = new TopMallsStatePagerAdapter(getFragmentManager(), malls);

        topMallsPager = (ViewPager) v.findViewById(R.id.top_malls_pager);

        ProgressBar pbTopMalls = (ProgressBar) v.findViewById(R.id.top_malls_progress);

        topMallsPager.setAdapter(topMallsAdapter);

        loadTopMalls(pbTopMalls);
    }

    private void loadTopMalls(ProgressBar pbTopMalls)
    {
        TopMallsTask topMallsTask = new TopMallsTask(activity, topMallsAdapter, topMallsPager,
                                                     pbTopMalls);

        String cache = topMallsTask.getCache();

        if(cache != null && !isRefreshed)
        {
            topMallsTask.setData(cache);
        }
        else
        {
            topMallsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "malls");
        }
    }

    private void initAndLoadDealsOfTheDay() {

        Logger.print("Viewed Rated Called");
        /*DealsTask dealsTask = new DealsTask(activity, listAdapter, null, null, this);

        String cache = dealsTask.getCache("dealofday");

        if(cache != null && !isRefreshed)
        {
            dealsTask.setData(cache);
        }
        else
        {
            dealsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "dealofday");
        }*/

        ViewedRatedTask viewedRatedTask = new ViewedRatedTask(activity, viewedRatedAdapter, this);

        String cache = viewedRatedTask.getCache("viewednrated");

        if(cache != null && !isRefreshed)
        {
            viewedRatedTask.setData(cache);
        }
        else {
            viewedRatedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "viewednrated");
        }
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
    public void filterTagUpdate() {

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

       /* int featuredCount = featuredAdapter.getCount();

        if(featuredCount > 0)
        {
            featuredSlider.start(featuredCount);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        promoSlider.stop();
       // featuredSlider.stop();
    }

    @Override
    public void onRefresh() {

        diskCache.remove(promoAdapter.deals);

        for(DOD dod : viewedRatedAdapter.dods)
        {
            diskCache.remove(dod.deals);

            memoryCache.remove(dod.deals);
        }

        diskCache.removeBrands(topBrandsStatePagerAdapter.brands);
        diskCache.removeMalls(topMallsAdapter.malls);


        memoryCache.remove(promoAdapter.deals);

        memoryCache.removeBrands(topBrandsStatePagerAdapter.brands);
        memoryCache.removeMalls(topMallsAdapter.malls);

        isRefreshed = true;

        loadPromos(null);

        loadTopBrands(null);
       // loadTopMalls(null);
        initAndLoadDealsOfTheDay();

        final String KEY = PREFIX_DEALS.concat("dealofday");
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        clearCache(activity, KEY);
        clearCache(activity, UPDATE_KEY);

        isRefreshed = false;
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {
       // tvDealsOfTheDay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals(int stringResId) {
        //tvDealsOfTheDay.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCompatActivity.RESULT_OK)
        {
            boolean isFav = data.getBooleanExtra("is_fav", false);

            listAdapter.genericDeal.isFav = isFav;

            String voucher = data.getStringExtra("voucher");

            if(voucher != null)
            {
                listAdapter.genericDeal.voucher = voucher;
                listAdapter.genericDeal.status = "Available";
            }

            int views = data.getIntExtra("views", -1);

            listAdapter.genericDeal.views = views;

            listAdapter.notifyDataSetChanged();
        }
    }
}