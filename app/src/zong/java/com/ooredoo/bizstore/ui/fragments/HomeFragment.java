package com.ooredoo.bizstore.ui.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.adapters.PromoStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.adapters.ViewedRatedAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
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
import com.ooredoo.bizstore.model.Category;
import com.ooredoo.bizstore.model.DOD;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.MyScroller;
import com.ooredoo.bizstore.utils.SliderUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.clearCache;

/**
 * @author Babar
 */
public class HomeFragment extends Fragment implements OnFilterChangeListener,
                                                      OnDealsTaskFinishedListener,
                                                      SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
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

    LinearLayout llContainer;

    private LayoutInflater inflater;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        BizStore bizStore = (BizStore) getActivity().getApplication();
        Tracker tracker = bizStore.getDefaultTracker();
        tracker.setScreenName("Dashboard");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    TextView tvGreetings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_dashboard, container, false);

        tvGreetings = (TextView) v.findViewById(R.id.greetings);

        greet();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            v.findViewById(R.id.scrollView).setNestedScrollingEnabled(true);
        }

        this.inflater = inflater;
        dealofDayCalled = false;
        init(v);

        return v;
    }

    private void greet()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        System.out.println("Hour: "+hour);

        if(hour >= 5 && hour < 12)
        {
            tvGreetings.setText("Good Morning");
        }
        else
        if ((hour >= 12 && hour <= 18))
        {
            tvGreetings.setText("Good Evening");
        }
        else
            {
                tvGreetings.setText("Good Night");
            }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.action_filter).setVisible(false);
    }

    boolean dealofDayCalled = false;

    int reqWidth, reqHeight;

    DisplayMetrics displayMetrics;

    TextView tvTopMalls;
    RelativeLayout rlTopMall;
    private void init(View v) {

        activity = (HomeActivity) getActivity();

        Resources resources = getResources();

        displayMetrics = resources.getDisplayMetrics();

        reqWidth = displayMetrics.widthPixels / 2;
        reqHeight = reqWidth;

        // This was killing the whole thing!
        //activity.setCurrentFragment(this);

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setSwipeableChildrens(R.id.scrollView, R.id.appBar);

       // final ListView listView = (ListView) v.findViewById(R.id.home_list_view);

       // LayoutInflater inflater = activity.getLayoutInflater();

      //  View header = inflater.inflate(R.layout.layout_dashboard, null);

        TextView tvTopBrands = (TextView) v.findViewById(R.id.top_brands);
        FontUtils.setFont(activity, tvTopBrands);

        llContainer = (LinearLayout) v.findViewById(R.id.container);

        tvTopMalls = (TextView) v.findViewById(R.id.top_malls);
        FontUtils.setFont(activity, tvTopMalls);

        rlTopMall = (RelativeLayout) v.findViewById(R.id.top_mall_layout);

        List<DOD> dods = new ArrayList<>();

        viewedRatedAdapter = new ViewedRatedAdapter(activity, R.layout.layout_deal_of_day, dods);

        initAndLoadPromotions(v);

        // initAndLoadTopBrands(v);

        initAndLoadTopMalls(v);

        initAndLoadDealsOfTheDay();
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

        promoAdapter = new PromoStatePagerAdapter(getChildFragmentManager(), deals, promoSlider);

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

        //ChildFragmentManager is very important
        //otherwise viewpager will loss state upon navigation
        topBrandsStatePagerAdapter = new TopBrandsStatePagerAdapter(getChildFragmentManager(), brands);

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

        //ChildFragmentManager is very important
        //otherwise viewpager will loss state upon navigation
        topMallsAdapter = new TopMallsStatePagerAdapter(getChildFragmentManager(), malls);

        topMallsPager = (ViewPager) v.findViewById(R.id.top_malls_pager);

        ProgressBar pbTopMalls = (ProgressBar) v.findViewById(R.id.top_malls_progress);

        topMallsPager.setAdapter(topMallsAdapter);

        loadTopMalls(pbTopMalls);
    }

    private void loadTopMalls(ProgressBar pbTopMalls)
    {
        TopMallsTask topMallsTask = new TopMallsTask(activity, topMallsAdapter, topMallsPager,
                                                     pbTopMalls, tvTopMalls, rlTopMall);

        String cache = topMallsTask.getCache();

        if(cache != null && !isRefreshed)
        {
            topMallsTask.setData(cache);
        }
        else
        {
            topMallsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    public void addMostViewedAndTopRated(List<DOD> dods)
    {if(isAdded())
    {
        llContainer.removeAllViews();

        int row = 0;
        int column = 0;
        for (DOD dod : dods) {

            row += 1;

            View v = inflater.inflate(R.layout.layout_horizontal_rated_viewed, null);

            for (GenericDeal genericDeal : dod.deals) {

                column += 1;

                LinearLayout llHorizontalLayout = (LinearLayout) v.findViewById(R.id.horizontal_layout);

                TextView tvCategory = (TextView) v.findViewById(R.id.category);

                Category category = Converter.convertCategoryText(activity, dod.category);
                tvCategory.setText(category.name);

                FontUtils.setFont(activity, tvCategory);


                View gridDealOfDay = inflater.inflate(R.layout.grid_generic, null);
                gridDealOfDay.setTag(genericDeal);
                gridDealOfDay.setOnClickListener(this);

                ImageView ivThumbnail = (ImageView) gridDealOfDay.findViewById(R.id.thumbnail);

                ProgressBar progressBar = (ProgressBar) gridDealOfDay.findViewById(R.id.progress_bar);

                TextView tvDescription = (TextView) gridDealOfDay.findViewById(R.id.desc);
                tvDescription.setText(genericDeal.description);

                TextView tvValidity = (TextView) gridDealOfDay.findViewById(R.id.validity);
                tvValidity.setText("Valid Till: " + genericDeal.endDate);

                Image image = genericDeal.image;

                if (image != null && image.gridBannerUrl != null && !image.gridBannerUrl.isEmpty())
                {
                    String imageUrl = BaseAsyncTask.IMAGE_BASE_URL + image.gridBannerUrl;

                    Bitmap bitmap = memoryCache.getBitmapFromCache(imageUrl);

                    if (bitmap != null)
                    {
                        progressBar.setVisibility(View.GONE);

                        ivThumbnail.setImageBitmap(bitmap);
                    }
                    else
                        {
                            new CommonHelper().fallBackToDiskCache(getActivity(), imageUrl, diskCache,
                                memoryCache, ivThumbnail, progressBar, reqWidth, reqHeight);
                        }
                }
                else
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params.width = displayMetrics.widthPixels / 2;
                params.height = params.width + (int) ((params.width * (5f / 100f)));

                if (column > 1) {
                    params.leftMargin = (int) Converter.convertDpToPixels(12);
                }

                llHorizontalLayout.addView(gridDealOfDay, params);
            }

            column = 0;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            llContainer.addView(v, params);
        }
    }
    else
    {
        Logger.print("Removed");
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
    public void filterTagUpdate() {}

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

     //   diskCache.removeBrands(topBrandsStatePagerAdapter.brands);
        diskCache.removeMalls(topMallsAdapter.malls);

        memoryCache.remove(promoAdapter.deals);

     //   memoryCache.removeBrands(topBrandsStatePagerAdapter.brands);
        memoryCache.removeMalls(topMallsAdapter.malls);

        isRefreshed = true;

        loadPromos(null);

       // loadTopBrands(null);
        loadTopMalls(null);
        initAndLoadDealsOfTheDay();

        final String KEY = PREFIX_DEALS.concat("viewednrated");
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, DealDetailActivity.class);
        intent.putExtra("generic_deal",(GenericDeal) v.getTag());

        activity.startActivity(intent);
    }
}