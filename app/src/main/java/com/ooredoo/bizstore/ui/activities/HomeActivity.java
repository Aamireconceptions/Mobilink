package com.ooredoo.bizstore.ui.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.HomePagerAdapter;
import com.ooredoo.bizstore.adapters.PopularSearchesGridAdapter;
import com.ooredoo.bizstore.adapters.RecentSearchesAdapter;
import com.ooredoo.bizstore.adapters.SearchBaseAdapter;
import com.ooredoo.bizstore.adapters.SearchSuggestionsAdapter;
import com.ooredoo.bizstore.asynctasks.AccountDetailsTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.CheckSubscriptionTask;
import com.ooredoo.bizstore.asynctasks.GCMRegisterTask;
import com.ooredoo.bizstore.asynctasks.SearchKeywordsTask;
import com.ooredoo.bizstore.asynctasks.SearchSuggestionsTask;
import com.ooredoo.bizstore.asynctasks.SearchTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.interfaces.ScrollToTop;
import com.ooredoo.bizstore.listeners.DiscountOnSeekChangeListener;
import com.ooredoo.bizstore.listeners.DrawerChangeListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.listeners.HomeTabSelectedListener;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.listeners.SubCategoryChangeListener;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.KeywordSearch;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.ui.fragments.NearbyFragment;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.GcmPreferences;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.NavigationMenuUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.StringUtils;
import com.ooredoo.bizstore.views.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.MAX_ALPHA;
import static com.ooredoo.bizstore.AppData.searchResults;
import static com.ooredoo.bizstore.AppData.searchSuggestions;
import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.APP_LANGUAGE;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

public class HomeActivity extends AppCompatActivity implements OnClickListener, OnKeyListener,
        OnFilterChangeListener, TextView.OnEditorActionListener, OnSubCategorySelectedListener,
        LocationListener {
    public static boolean rtl = false;

    public DrawerLayout drawerLayout;
    private DrawerChangeListener mDrawerListener = new DrawerChangeListener(this);

    public GridView mPopularSearchGridView;
    public ListView mRecentSearchListView, mSearchResultsListView, mSearchSuggestionListView;

    public SearchSuggestionsAdapter mSearchSuggestionsAdapter;
    public RecentSearchesAdapter mRecentSearchesAdapter;
    public PopularSearchesGridAdapter mPopularSearchesGridAdapter;

    public SearchBaseAdapter mSearchResultsAdapter;

    public PopupWindow searchPopup;
    public ActionBar mActionBar;

    Menu mMenu;
    MenuItem loaderItem;

    public AutoCompleteTextView acSearch;

    public HomePagerAdapter homePagerAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExpandableListView expandableListView;

    public boolean isSearchEnabled = false;
    public boolean isSearchTextWatcherEnabled = true;

    public boolean doApplyDiscount = false;

    public boolean doApplyRating = true;

    public String ratingFilter, distanceFilter;

    public int minDiscount = 0, maxDiscount = 0;

    public View searchView;

    public RangeSeekBar<Integer> rangeSeekBar;

    private TextView tvRating1, tvRating2, tvRating3, tvRating4, tvRating5;

    SubCategoryChangeListener subCategoryChangeListener;

    public static String searchType = "deals";

    public static boolean isShowResults = false;

    public TextView searchDeals, searchBusinesses;

    public static ImageView profilePicture;

    public static Dialog loader;

    private LinearLayout llTopDeals;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPrefUtils sharedPrefUtils;

    private GcmPreferences gcmPreferences;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private HomeActivity activity = this;

    private long time = 10 * 60 * 1000;

    public static TextView tvName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = SharedPrefUtils.getStringVal(this, "username");
        String password = SharedPrefUtils.getStringVal(this, "password");

        HomeActivity.lat = SharedPrefUtils.getFloatValue(this, "lat");
        HomeActivity.lng = SharedPrefUtils.getFloatValue(this, "lng");

        if(!username.equals(SharedPrefUtils.EMPTY)) {
            BizStore.username = username;
        }

        if(!password.equals(SharedPrefUtils.EMPTY)) {
            BizStore.password = password;
        }

        CategoryUtils.setUpSubCategories(this);

        String language = SharedPrefUtils.getStringVal(this, APP_LANGUAGE);

        if(StringUtils.isNotNullOrEmpty(language)) {
            BizStore.setLanguage(language);
            NavigationMenuOnClickListener.updateConfiguration(this, language);
        }

        Logger.print("HomeActivity onCreate");
        overrideFonts();
        setContentView(R.layout.activity_home);

        CategoryUtils.setUpSubCategories(this);

        init();

        registeredWithGcmIfRequired();

        /*new SearchKeywordsTask(this).execute();

        new AccountDetailsTask().execute(BizStore.username);*/

        new SearchKeywordsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        tvName = (TextView) findViewById(R.id.name);

        new AccountDetailsTask(tvName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BizStore.username);

        BizStore.forceStopTasks = false;

        checkIfGpsEnabled();

        if(!BuildConfig.FLAVOR.equals("dealionare"))
        startSubscriptionCheck();
    }

    Timer timer;

    private void startSubscriptionCheck() {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CheckSubscriptionTask subscriptionTask = new CheckSubscriptionTask(HomeActivity.this, timer);
                        subscriptionTask.execute();
                    }
                });
            }
        }, 0, time);
    }

    private void overrideFonts() {
        BizStore bizStore = (BizStore) getApplicationContext();
        bizStore.overrideDefaultFonts();
    }

    LocationManager locationManager;

    int minTimeMillis = 10 * (60 * 1 * 1000);
    int distanceMeters = 50;
    RelativeLayout filterParent;
    public FloatingActionButton fab;
    private void init() {
        diskCache.requestInit(this);

        sharedPrefUtils = new SharedPrefUtils(this);

        gcmPreferences = new GcmPreferences(this);

        RecentSearchesAdapter.homeActivity = this;

        initFilter();

        searchView = getLayoutInflater().inflate(R.layout.search_popup, null);
        searchPopup = new PopupWindow(searchView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        searchDeals = (TextView) findViewById(R.id.search_deals);
        searchBusinesses = (TextView) findViewById(R.id.search_business);

        if(BuildConfig.FLAVOR.equals("mobilink"))
        {
            searchBusinesses.setVisibility(View.GONE);
        }

        setSearchCheckboxSelection();

        searchDeals.setOnClickListener(this);
        searchBusinesses.setOnClickListener(this);

        homePagerAdapter = new HomePagerAdapter(this, getFragmentManager());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

       /* if(BizStore.getLanguage().equals("en"))
        {
            Logger.print("Drawer: " + BizStore.getLanguage());

            DrawerLayout.LayoutParams params = new DrawerLayout
                    .LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);

            params.gravity = Gravity.LEFT;

            expandableListView.setLayoutParams(params);

            DrawerLayout.LayoutParams params2 =new DrawerLayout
                    .LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
            params2.gravity = Gravity.RIGHT;

            filterParent.setLayoutParams(params2);

           // drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
           // drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
           drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        }
        else
        {
            Logger.print("Drawer: " + BizStore.getLanguage());

            DrawerLayout.LayoutParams params = new DrawerLayout
                    .LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);

            params.gravity = Gravity.LEFT;

            filterParent.setLayoutParams(params);


            DrawerLayout.LayoutParams params2 =new DrawerLayout
                    .LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);

            params2.gravity = Gravity.RIGHT;

            expandableListView.setLayoutParams(params2);

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
        }*/

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        drawerLayout.setDrawerListener(mDrawerListener);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMillis, distanceMeters, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null) {
            Logger.print("Location Changed#LastKnown: " + location.getLatitude() + ", " + location.getLongitude());

            SharedPrefUtils.updateVal(this, "lat", (float) location.getLatitude());
            SharedPrefUtils.updateVal(this, "lng", (float) location.getLongitude());
        }

        viewPager = (ViewPager) findViewById(R.id.home_viewpager);



        mSearchResultsListView = (ListView) findViewById(R.id.lv_search_results);
        mRecentSearchListView = (ListView) findViewById(R.id.list_recent_searches);
        mPopularSearchGridView = (GridView) findViewById(R.id.grid_popular_searches);
        mSearchSuggestionListView = (ListView) searchView.findViewById(R.id.list_search_suggestions);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            if(BizStore.getLanguage().equals("ar"))
                mPopularSearchGridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._minus2sdp));
   /*             else
                    gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._8sdp));*/
        }

        setRecentSearches();

        mPopularSearchesGridAdapter = new PopularSearchesGridAdapter(this, R.layout.list_popular_search, new ArrayList<KeywordSearch>());
        mPopularSearchGridView.setAdapter(mPopularSearchesGridAdapter);

        setSearchSuggestions(new ArrayList<String>());

        acSearch = (AutoCompleteTextView) findViewById(R.id.ac_search);

        llSearch = (LinearLayout) findViewById(R.id.layout_search);

       /* swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setOnRefreshListener(this);*/

        fab = (FloatingActionButton) findViewById(R.id.scrollToTop);
        fab.setOnClickListener(this);

        setupSearchField();

        NavigationMenuUtils navigationMenuUtils = new NavigationMenuUtils(this, expandableListView);
        navigationMenuUtils.setupNavigationMenu();

        setupToolbar();

        setupTabs();

        setupPager();

        popularGrid = findViewById(R.id.grid_popular_searches);
        searchLayout = findViewById(R.id.layout_search_filter);
        searchResultView = findViewById(R.id.lv_search_results);
    }

    @Override
    protected void onResume() {
        super.onResume();

     /*   if(BizStore.getLanguage().equals("en"))
        {
            Logger.print("Drawer: "+BizStore.getLanguage());

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);

            // drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        }
        else
        {
            Logger.print("Drawer: "+BizStore.getLanguage());

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);

        }*/
    }

    public void setSearchSuggestions(List<String> list) {
        mSearchSuggestionsAdapter = new SearchSuggestionsAdapter(this, R.layout.list_search_suggestion, list);
        mSearchSuggestionListView.setAdapter(mSearchSuggestionsAdapter);
        int height = (int) Converter.convertDpToPixels(160);
        if(list != null && list.size() > 3) {
            mSearchSuggestionListView.getLayoutParams().height = height;
        }
        mSearchSuggestionListView.setVisibility(View.VISIBLE);
    }

    private void setupSearchField() {
        acSearch.setThreshold(1);

        acSearch.addTextChangedListener(new SearchTextWatcher());

        acSearch.setOnKeyListener(this);

        acSearch.setOnEditorActionListener(this);
    }

    public static AppBarLayout appBarLayout;
public CoordinatorLayout coordinatorLayout;
    private void setupToolbar() {

        appBarLayout = (AppBarLayout) findViewById(R.id.appBar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(BuildConfig.FLAVOR.equals("telenor"))
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.status_bar));
        }
        else
        if(BuildConfig.FLAVOR.equals("dealionare"))
        {
            toolbar.setBackgroundResource(R.drawable.dealionare_toolbar_bg);
        }

        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);

        View logoView;
        if(BuildConfig.FLAVOR.equals("telenor") || BuildConfig.FLAVOR.equals("dealionare"))
        {
            logoView = getLayoutInflater().inflate(R.layout.layout_actionbar, null);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(logoView);
        }
        else {
            boolean isArabic = BizStore.getLanguage().equals("ar");
            mActionBar.setLogo(isArabic ? R.drawable.ic_bizstore : R.drawable.ic_bizstore);

            logoView = getToolbarLogoIcon(toolbar);
        }

        logoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.print("Logo clicked");
                selectTab(BizStore.getLanguage().equals("en") ? 0 : 12);
            }
        });


    }

    public void resetToolBarPosition()
    {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        int[] consumed = new int[2];
        behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, -1000, consumed);

    }

    public static View getToolbarLogoIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        /*if(BuildConfig.FLAVOR.equals("dealionare"))
        {
            tabLayout.setBackgroundColor(Color.parseColor("#232f3e"));
        }*/

        //tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

      // tabLayout.setOnTabSelectedListener(new HomeTabSelectedListener(this, viewPager));

       // tabLayout.addTab(tabLayout.newTab());

        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);


    }

    private void setupPager() {
        viewPager.setAdapter(null);
        viewPager.setAdapter(homePagerAdapter);
       //viewPager.addOnPageChangeListener(new HomeTabLayoutOnPageChangeListener(tabLayout, this));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new HomeTabSelectedListener(this, viewPager));

        if(BizStore.lastTab != -1 && BizStore.lastTab != 0) {
            Logger.print("LastTab: "+BizStore.lastTab);
            final int correctTab;

            if (BizStore.getLanguage().equals("en")) {
                correctTab = 12 - BizStore.lastTab;
            }
            else {
                correctTab = Math.abs(BizStore.lastTab - 12);
            }

            Logger.print("CorrectTab: " + correctTab);

            tabLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tabLayout.getTabAt(correctTab).select();
                }
            }, 500);
        }
        else
        {
            tabLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int pos = BizStore.getLanguage().equals("en") ? 0 : 12;

                    tabLayout.getTabAt(pos).select();
                }
            }, 500);

          /*  if(BizStore.getLanguage().equals("ar")) {

                tabLayout.getTabAt(12).select();
            }
            else
            {
                tabLayout.getTabAt(0).select();
            }*/
        }

        //tabLayout.getTabAt(11).select();

        //viewPager.setOffscreenPageLimit(11);Dea
       /* if(BizStore.getLanguage().equals("ar"))
        {
            viewPager.setCurrentItem(homePagerAdapter.getCount() - 1);ac
        }*/

       /* if(BizStore.getLanguage().equals("ar"))
        {
            Logger.print("Lang Arabic");
           *//* viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(homePagerAdapter.getCount() - 1, false);
                }
            }, 1000);*//*


            TabLayout.Tab tab = tabLayout.getTabAt(homePagerAdapter.getCount() - 1);
            tab.select();
        }
        else
        {
            Logger.print("Lang Eng");

            *//*viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(0, false);
                }
            }, 1000);*//*

            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        }*/

    }

    TextView tvDistance5, tvDistance10, tvDistance20, tvDistance35, tvDistance50;

    private void initFilter() {
        FilterOnClickListener clickListener = new FilterOnClickListener(this, 0);

        ImageView ivBack = (ImageView) findViewById(R.id.back);
        ivBack.setOnClickListener(clickListener);

        TextView tvDone = (TextView) findViewById(R.id.done);
        tvDone.setOnClickListener(clickListener);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            tvDone.setBackgroundResource(R.drawable.white_btn_ripple);
        }

        /*TextView tvDealsAndDiscount = (TextView) findViewById(R.id.deals_discount_checkbox);
        tvDealsAndDiscount.setOnClickListener(clickListener);

        TextView tvBusinessAndDirectory = (TextView) findViewById(R.id.business_directory_checkbox);
        tvBusinessAndDirectory.setOnClickListener(clickListener);*/

        String sort = getString(R.string.sort).toUpperCase();
        String by = getString(R.string.by).toUpperCase();

        TextView tvSortBy = (TextView) findViewById(R.id.sort_by);
        tvSortBy.setText(sort + " " + by, TextView.BufferType.SPANNABLE);

        int color = BuildConfig.FLAVOR.equals("ooredoo") || BuildConfig.FLAVOR.equals("mobilink")
                ? R.color.red : R.color.white;
        Spannable spannable = (Spannable) tvSortBy.getText();
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(color)),
                0, sort.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView tvRating = (TextView) findViewById(R.id.rating_checkbox);
        tvRating.setOnClickListener(clickListener);

        tvRating1 = (TextView) findViewById(R.id.rating_1);
        tvRating1.setOnClickListener(clickListener);

        tvRating2 = (TextView) findViewById(R.id.rating_2);
        tvRating2.setOnClickListener(clickListener);

        tvRating3 = (TextView) findViewById(R.id.rating_3);
        tvRating3.setOnClickListener(clickListener);

        tvRating4 = (TextView) findViewById(R.id.rating_4);
        tvRating4.setOnClickListener(clickListener);

        tvRating5 = (TextView) findViewById(R.id.rating_5);
        tvRating5.setOnClickListener(clickListener);

        tvDistance5 = (TextView) findViewById(R.id._5);
        tvDistance5.setOnClickListener(clickListener);

        tvDistance10 = (TextView) findViewById(R.id._10);
        tvDistance10.setOnClickListener(clickListener);

        tvDistance20 = (TextView) findViewById(R.id._20);
        tvDistance20.setOnClickListener(clickListener);

        tvDistance35 = (TextView) findViewById(R.id._35);
        tvDistance35.setOnClickListener(clickListener);

        tvDistance50 = (TextView) findViewById(R.id._50);
        tvDistance50.setOnClickListener(clickListener);

        TextView tvDiscount = (TextView) findViewById(R.id.discount_checkbox);
        tvDiscount.setOnClickListener(clickListener);

        findViewById(R.id.cb_highest_discount).setOnClickListener(clickListener);

        rangeSeekBar = (RangeSeekBar) findViewById(R.id.discount_seekbar);
        rangeSeekBar.setEnabled(false);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new DiscountOnSeekChangeListener(this));

        CategoryUtils.subCategories.clear();
        CategoryUtils.setUpSubCategories(this);
        subCategoryChangeListener = new SubCategoryChangeListener(this);

        resetFilters();
    }

    public void resetFilters() {
        tvRating1.setSelected(false);
        tvRating2.setSelected(false);
        tvRating3.setSelected(false);
        tvRating4.setSelected(false);
        tvRating5.setSelected(false);
        CheckBox discountCheckBox = (CheckBox) findViewById(R.id.cb_highest_discount);
        doApplyDiscount = false;
        discountCheckBox.setChecked(doApplyDiscount);
        discountCheckBox.setText(getString(R.string.sort_discount));

        doApplyRating = false;
        ratingFilter = null;

        distanceFilter = null;

        tvDistance5.setSelected(false);
        tvDistance10.setSelected(false);
        tvDistance20.setSelected(false);
        tvDistance35.setSelected(false);
        tvDistance50.setSelected(false);

        CategoryUtils.resetCheckboxes();
    }

    private void checkIfGpsEnabled()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            DialogUtils.createLocationDialog(this).show();
        }
    }

    private void registeredWithGcmIfRequired() {
        if(isGPlayServicesAvailable()) {
            Logger.print("GPlayServicesAvailable");

            String deviceGcmToken = gcmPreferences.getDeviceGCMToken();

            Logger.print("deviceGcmToken: " + deviceGcmToken);

            String userGcmId = gcmPreferences.getUserGCMToken(BizStore.username + "_" + deviceGcmToken);

            Logger.print("userGcmId: " + userGcmId);

            if(userGcmId != null) {
                Logger.print("Skipping gcmRegistering. User already registered");
                return;
            }

            Logger.print("Performing GCM registration");

            GCMRegisterTask gcmRegisterTask = new GCMRegisterTask(this, gcmPreferences);
            gcmRegisterTask.execute();
        } else
            Logger.print("GPlayServices not available");
    }

    public void setRatingEnabled(boolean enabled) {
        tvRating1.setEnabled(enabled);
        tvRating2.setEnabled(enabled);
        tvRating3.setEnabled(enabled);
        tvRating4.setEnabled(enabled);
        tvRating5.setEnabled(enabled);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static MenuItem miSearch;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        mMenu = menu;
        loaderItem = mMenu.findItem(R.id.action_loading);
        loaderItem.setActionView(R.layout.loader);
        loaderItem.setVisible(false);

        miSearch = menu.findItem(R.id.action_search);

        return true;
    }

    public  void clickSearch()
    {
        onOptionsItemSelected(miSearch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            Logger.print("as");
            if(searchPopup.isShowing())
                searchPopup.dismiss();
            if(isSearchEnabled) {
                HomeActivity.isShowResults = false;
                acSearch.setText("");
                acSearch.setHint(R.string.search);
                hideSearchResults();
                hideSearchPopup();


                //viewPager.invalidate();
                //viewPager.requestFocus();
                //showHideSearchBar(false);
            } else {
                showHideDrawer(GravityCompat.START, true);
            }
        } else if(id == R.id.action_search || id == R.id.search) {
            boolean show = id == R.id.action_search;
            if(!show) {
                isShowResults = false;
                acSearch.setText("");
                acSearch.setHint(R.string.search);
            } else {
                if(searchSuggestions == null || searchSuggestions.list == null || searchSuggestions.list.size() == 0) {
                    new SearchSuggestionsTask(this).execute();
                }
            }
            showHideSearchBar(show);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        diskCache.requestFlush();
    }

    public void hideSearchResults() {


        isShowResults = false;

        llSearch.setVisibility(View.VISIBLE);
        //findViewById(R.id.layout_search).setVisibility(View.VISIBLE);
       showHideSearchBar(false);
    }

    public void showHideDrawer(int gravity, boolean show) {
        if(show) {
            drawerLayout.openDrawer(gravity);
        } else {
            drawerLayout.closeDrawer(gravity);
        }
    }

    View popularGrid, searchLayout, searchResultView;

    public void showHideSearchBar(boolean show) {
        isSearchEnabled = show;
        mMenu.findItem(R.id.search).setVisible(show);
        mMenu.findItem(R.id.action_search).setVisible(!show);
        mActionBar.setDisplayUseLogoEnabled(!show);
        popularGrid.setVisibility(show ? View.VISIBLE : View.GONE);
        searchLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mRecentSearchListView.setVisibility(show ? View.VISIBLE : View.GONE);
        searchResultView.setVisibility(View.GONE);
        if(show) {
            setRecentSearches();
            setPopularSearches(AppData.popularSearches.list);
        }

        mActionBar.setHomeAsUpIndicator(show ? R.drawable.ic_action_back : R.drawable.ic_menu);
        acSearch.setVisibility(show ? View.VISIBLE : View.GONE);

        acSearch.requestFocus();

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        // List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, metaState);

        // Dispatch touch event to view
       // acSearch.dispatchTouchEvent(motionEvent);



        if(show) {
           /* new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSearchPopup();
                }
            }, 100);*/

            showSearchPopup();

            if(fab.getVisibility() == View.VISIBLE)
            {
                AnimatorUtils.hideFab(fab);
            }

        } else {
            hideSearchPopup();
        }
    }
LinearLayout llSearch;
    public void showSearchPopup() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        isShowResults = false;
       // tabLayout.setAlpha(0f);
        //viewPager.setAlpha(0f);
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        setRecentSearches();
        setPopularSearches(AppData.popularSearches.list);
       // findViewById(R.id.layout_search).setVisibility(View.VISIBLE);

        llSearch.setVisibility(View.VISIBLE);
        mRecentSearchListView.setVisibility(View.VISIBLE);
        findViewById(R.id.layout_search_filter).setVisibility(View.GONE);
        //searchPopup.showAsDropDown(acSearch, 10, 25);
    }

    public void setRecentSearches() {
        List<SearchItem> searchItems = new Select().all().from(SearchItem.class).orderBy("id DESC").execute();
        for(SearchItem searchItem : searchItems) {
            Logger.logI("Search Item", searchItem.id + ", " + searchItem.keyword);
        }
        Logger.print("RECENT_SEARCH_ITEM_COUNT: " + searchItems.size());
        mRecentSearchesAdapter = new RecentSearchesAdapter(this, R.layout.list_item_recent_search, searchItems);
        mRecentSearchesAdapter.setShowResultCount(false);
        mRecentSearchListView.setAdapter(mRecentSearchesAdapter);
        int height = (int) Converter.convertDpToPixels(160);
        if(searchItems.size() > 3) {
            mRecentSearchListView.getLayoutParams().height = height;
        }
        mRecentSearchListView.setVisibility(searchItems.size() == 0 ? View.GONE : View.VISIBLE);
    }

    public void setPopularSearches(List<KeywordSearch> list) {
        if(list != null) {
            mPopularSearchesGridAdapter = new PopularSearchesGridAdapter(this, R.layout.list_popular_search, list);
            mPopularSearchGridView.setAdapter(mPopularSearchesGridAdapter);
            findViewById(R.id.layout_popular_searches).setVisibility(list.size() == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public void hideSearchPopup() {

        isShowResults = true;
        //findViewById(R.id.layout_search).setVisibility(View.GONE);

        llSearch.setVisibility(View.GONE);
        mSearchResultsListView.setVisibility(View.GONE);
        mRecentSearchListView.setVisibility(View.GONE);
       // tabLayout.setAlpha(MAX_ALPHA);
        //viewPager.setAlpha(MAX_ALPHA);

        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        //searchPopup.dismiss();
        //acSearch.setText(null);

    }

    public void selectTab(int tabPosition) {

       //viewPager.setCurrentItem(tabPosition, true);

        tabLayout.getTabAt(tabPosition).select();
    }

    @Override
    public void onSubCategorySelected() {
        ((OnSubCategorySelectedListener) currentFragment).onSubCategorySelected();
    }

    public static double lat;
    public static double lng;

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        Logger.print("Location Changed: " + lat + ", " + lng);

        SharedPrefUtils.updateVal(this, "lat", (float) lat);
        SharedPrefUtils.updateVal(this, "lng", (float) lng);

        int nearbyIndex = BizStore.getLanguage().equals("en") ? 1 : 11;

       Fragment nearbyFragment = getFragmentManager().findFragmentByTag("android:switcher:"
               + R.id.home_viewpager + ":" + nearbyIndex);

        if(nearbyFragment != null && !BuildConfig.FLAVOR.equals("mobilink"))
        {
            ((NearbyFragment) nearbyFragment).onLocationFound();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setSearchCheckboxSelection() {
        searchDeals.setSelected(searchType.equals("deals"));
        searchBusinesses.setSelected(!searchType.equals("deals"));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        List<SearchResult> results;

        if(viewId == R.id.search_deals || viewId == R.id.search_business) {
            //((CheckBox)searchView.findViewById(v.getId())).setChecked(((CheckBox)v).isChecked());
            boolean showDeals = (viewId == R.id.search_deals);
            searchType = showDeals ? "deals" : "business";
            Logger.print("SEARCH_FILTER: " + searchType);
            setSearchCheckboxSelection();
            if(searchResults != null) {
                if(searchResults.list != null & searchResults.list.size() > 0) {
                    if(searchType.equalsIgnoreCase("business")) {
                        results = getBusinesses();
                        mSearchResultsListView.setDivider(new ColorDrawable(getResources().getColor(R.color.grey)));
                        mSearchResultsListView.setDividerHeight(1);

                    } else {
                        mSearchResultsListView.setDivider(new ColorDrawable(getResources().getColor(R.color.white)));
                        mSearchResultsListView.setDividerHeight((int) Converter.convertDpToPixels(getResources().getDimension(R.dimen._6sdp)));
                        results = getDeals();
                    }
                    String keyword = acSearch.getText().toString();
                    setupSearchResults(keyword, results, false);
                }
            }
            Logger.print("SEARCH_FILTER: " + searchType);
        }
        else
            if(viewId == R.id.scrollToTop)
            {
                if(!(currentFragment instanceof HomeFragment))
                ((ScrollToTop) currentFragment).scroll();
            }
    }

    public static List<SearchResult> getBusinesses() {
        List<SearchResult> results = new ArrayList<>();
        for(SearchResult result : searchResults.list) {
            if(isNotNullOrEmpty(result.type) && result.type.equalsIgnoreCase("business")) {
                results.add(result);
            }
        }
        return results;
    }

    public static List<SearchResult> getDeals() {
        List<SearchResult> results = new ArrayList<>();
        for(SearchResult result : searchResults.list) {
            if(isNotNullOrEmpty(result.type)) {
                if(!result.type.equalsIgnoreCase("business")) {
                    results.add(result);
                }
            } else {
                results.add(result);
            }
        }
        return results;
    }

    /*public void showDetailActivity(int detailType, String dealCategory, int typeId) {
        Intent intent = new Intent();
        intent.setClass(this, detailType == BUSINESS ? BusinessDetailActivity.class : DealDetailActivity.class);
        intent.putExtra(AppConstant.ID, typeId);
        intent.putExtra(CATEGORY, dealCategory);
        startActivity(intent);
    }*/

    public void showDealDetailActivity(String dealCategory, GenericDeal genericDeal) {
        Intent intent = new Intent();
        intent.setClass(this, DealDetailActivity.class);
        intent.putExtra("generic_deal", genericDeal);
        intent.putExtra(CATEGORY, dealCategory);
        startActivity(intent);
    }

    public void showBusinessDetailActivity(String dealCategory, Business business) {
        Intent intent = new Intent();
        intent.setClass(this, BusinessDetailActivity.class);
        intent.putExtra("business", business);
        intent.putExtra(CATEGORY, dealCategory);
        startActivity(intent);
    }

    private Fragment currentFragment;

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    @Override
    public void onFilterChange() {
        Logger.print("HomeActivity onFilterChange Fragment: "+currentFragment.getClass().getName());

        ((OnFilterChangeListener) currentFragment).onFilterChange();
    }

    public void populateSearchResults(List<SearchResult> searchResultList) {
        //if(searchResultList.size() > 0) {
            mSearchResultsAdapter = new SearchBaseAdapter(this, R.layout.list_deal_promotional, searchResultList);
            mSearchResultsListView.setAdapter(mSearchResultsAdapter);
        //mSearchResultsAdapter.setData(searchResultList);
        //mSearchResultsAdapter.notifyDataSetChanged();
        /*} else {
            Toast.makeText(getApplicationContext(), R.string.error_no_data, LENGTH_SHORT).show();
            hideSearchResults();
            showHideSearchBar(true);
        }*/
    }

    public void setupSearchResults(String keyword, List<SearchResult> results, boolean isKeywordSearch) {

        isSearchTextWatcherEnabled = false;

        if(isNotNullOrEmpty(keyword)) {
            acSearch.setText(keyword);
        }

        if(results == null)
            results = new ArrayList<>();

        if(!isKeywordSearch && results.size() > 0) {
           // SearchItem searchItem = new SearchItem(0, keyword, results.size());
            //SearchItem.addToRecentSearches(searchItem);
        }

        hideSearchPopup();
        HomeActivity.isShowResults = true;
       // results = getDeals();
        populateSearchResults(results);

        findViewById(R.id.layout_popular_searches).setAlpha(MAX_ALPHA);

        mRecentSearchListView.setVisibility(View.GONE);
        //findViewById(R.id.layout_search).setVisibility(View.VISIBLE);

        llSearch.setVisibility(View.VISIBLE);
        findViewById(R.id.lv_search_results).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_popular_searches).setVisibility(View.GONE);
        findViewById(R.id.layout_search_filter).setVisibility(View.VISIBLE);

        searchDeals.setText(getDealsCount() + " " + getString(R.string.deals));
        searchBusinesses.setText(getBusinessCount() + " " + getString(R.string.businesses));

        isSearchTextWatcherEnabled = true;
    }

    private int getDealsCount() {
        int count = 0;
        for(SearchResult result : AppData.searchResults.list) {
            if(isNotNullOrEmpty(result.type)) {
                if(!result.type.equalsIgnoreCase("business")) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getBusinessCount() {
        int count = 0;
        for(SearchResult result : AppData.searchResults.list) {
            if(isNotNullOrEmpty(result.type)) {
                if(result.type.equalsIgnoreCase("business")) {
                    count++;
                }
            }
        }
        return count;
    }

    public void executeSearchTask(String keyword) {
        if(searchPopup != null && searchPopup.isShowing()) {
            searchPopup.dismiss();
        }
        searchType = "deals";
        setSearchCheckboxSelection();
        new SearchTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keyword);
    }


    public class SearchTextWatcher implements TextWatcher {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Logger.print("onTextChanged: " + s);
        }

        public void afterTextChanged(Editable edt) {
            if(isSearchTextWatcherEnabled) {
                if(edt.toString().length() == 0) {
                    mRecentSearchListView.setAlpha(MAX_ALPHA);
                    findViewById(R.id.layout_popular_searches).setAlpha(MAX_ALPHA);
                    searchPopup.dismiss();
                } else {
                    String filter = edt.toString();
                    List<String> filterResults = new ArrayList<>();
                    if(AppData.searchSuggestions != null) {
                        for(String suggestion : AppData.searchSuggestions.list) {
                            if(suggestion != null && suggestion.contains(filter)) {
                                filterResults.add(suggestion);
                            }
                        }
                    }
                    setSearchSuggestions(filterResults);
                    if(!searchPopup.isShowing()) {
                        mRecentSearchListView.setAlpha(0f);
                        findViewById(R.id.layout_popular_searches).setAlpha(0f);
                        int offset = (int) Converter.convertDpToPixels(15);
                        searchPopup.showAsDropDown(acSearch, 0, offset);
                    }
                }
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int viewId = v.getId();
        if(viewId == R.id.ac_search) {
            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                //performSearch(acSearch.getText().toString().trim());
                return true;
            }
        }
        return false;
    }

    public void performSearch(String keyword) {
        if(hasInternetConnection(this)) {
            //String keyword = v.getText().toString();
            if(isNotNullOrEmpty(keyword)) {
                Logger.print("SEARCH_KEYWORD: " + keyword);
                executeSearchTask(keyword);
            } else {
                makeText(acSearch.getContext(), getString(R.string.error_search_term), LENGTH_SHORT).show();
            }
        } else {
            makeText(getApplicationContext(), getString(R.string.error_no_internet), LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if((actionId & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEARCH) {
            performSearch(acSearch.getText().toString().trim());
            return true;
        }
        return false;
    }

    private boolean isGPlayServicesAvailable() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, code, 0).show();

            return false;
        }

        return true;
    }

    public void showLoader() {
        if(loaderItem != null) {
            loaderItem.setVisible(true);
            ProgressBar pb = (ProgressBar) loaderItem.getActionView().findViewById(R.id.progressBar);
            pb.setVisibility(View.VISIBLE);
            Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
            pb.startAnimation(rotateAnimation);
            ViewGroup.LayoutParams params = pb.getLayoutParams();
            int bounds = (int) Converter.convertDpToPixels(24);
            params.width = bounds;
            params.height = bounds;
            pb.setLayoutParams(params);
        }
    }

    public void hideLoader() {
        if(loaderItem != null) {
            ProgressBar pb = (ProgressBar) loaderItem.getActionView().findViewById(R.id.progressBar);
            pb.setVisibility(View.GONE);
            pb.clearAnimation();
            loaderItem.setVisible(false);
        }
    }

    public void filterTagUpdate()
    {
        if(currentFragment != null)
        ((OnFilterChangeListener) currentFragment).filterTagUpdate();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);

            return;
        }

        if(drawerLayout.isDrawerOpen(GravityCompat.END))
        {
            drawerLayout.closeDrawer(GravityCompat.END);
            return;
        }

        if(isSearchEnabled)
        {
            return;
        }

        BizStore.lastTab = -1;

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101)
        {
            if(resultCode == RESULT_OK)
            {
                if(currentFragment != null) {
                    ((SwipeRefreshLayout.OnRefreshListener) currentFragment).onRefresh();
                }
                else
                {
                    HomeFragment homeFragment = (HomeFragment) getFragmentManager().
                        findFragmentByTag("android:switcher:" + R.id.home_viewpager + ":" + 0);

                    if(homeFragment != null)
                    {
                        homeFragment.onRefresh();
                    }
                }


            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(searchPopup != null && searchPopup.isShowing())
            searchPopup.dismiss();

        memoryCache.tearDown();

        diskCache.requestClose();

        BitmapDownloadTask.downloadingPool.clear();

        Logger.print("HomeActivity onDestroy");

        locationManager.removeUpdates(this);

        AppData.searchSuggestions = null;

        BitmapDownloadTask.downloadingPool.clear();

        //timer.cancel();
    }


}