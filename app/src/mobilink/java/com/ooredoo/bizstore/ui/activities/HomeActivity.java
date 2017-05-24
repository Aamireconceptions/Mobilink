package com.ooredoo.bizstore.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
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
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.facebook.applinks.AppLinkData;
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
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.CheckSubscriptionTask;
import com.ooredoo.bizstore.asynctasks.GCMRegisterTask;
import com.ooredoo.bizstore.asynctasks.LocationUpdateTask;
import com.ooredoo.bizstore.asynctasks.SearchKeywordsTask;
import com.ooredoo.bizstore.asynctasks.SearchSuggestionsTask;
import com.ooredoo.bizstore.asynctasks.SearchTask;
import com.ooredoo.bizstore.interfaces.LocationChangeListener;
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
import com.ooredoo.bizstore.views.RangeSeekBar;

import net.hockeyapp.android.CrashManager;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.MAX_ALPHA;
import static com.ooredoo.bizstore.AppConstant.PROFILE_PIC_URL;
import static com.ooredoo.bizstore.AppData.searchResults;
import static com.ooredoo.bizstore.AppData.searchSuggestions;
import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * The main viewpager that holds all the fragments like home, food, top deals
 */
@EActivity
public class HomeActivity extends AppCompatActivity implements OnClickListener,
        OnFilterChangeListener, TextView.OnEditorActionListener, OnSubCategorySelectedListener,
        LocationListener {

    private DrawerChangeListener mDrawerListener = new DrawerChangeListener(this);

    @ViewById(R.id.drawer_layout)
    public DrawerLayout drawerLayout;

    @ViewById(R.id.grid_popular_searches)
    public GridView mPopularSearchGridView;

    @ViewById(R.id.list_recent_searches)
    public ListView mRecentSearchListView;

    @ViewById(R.id.lv_search_results)
    public ListView mSearchResultsListView;

    public SearchSuggestionsAdapter mSearchSuggestionsAdapter;
    public RecentSearchesAdapter mRecentSearchesAdapter;
    public PopularSearchesGridAdapter mPopularSearchesGridAdapter;
    public SearchBaseAdapter mSearchResultsAdapter;
    public HomePagerAdapter homePagerAdapter;

    public PopupWindow searchPopup;
    public ActionBar mActionBar;
    private Menu mMenu;

    @ViewById(R.id.ac_search)
    public AutoCompleteTextView acSearch;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.home_viewpager)
    ViewPager viewPager;

    @ViewById(R.id.expandable_list_view)
    ExpandableListView expandableListView;

    @ViewById(R.id.search_deals)
    public TextView searchDeals;

    @ViewById(R.id.search_business)
    public TextView searchBusinesses;

    public boolean isSearchEnabled = false;
    public boolean isSearchTextWatcherEnabled = true;
    public boolean doApplyDiscount = false;
    public boolean doApplyDistance = false;
    public boolean doApplyRating = true;

    public String ratingFilter, distanceFilter;

    public int minDiscount = 0, maxDiscount = 0;

    public View searchView;

    SubCategoryChangeListener subCategoryChangeListener;

    public static String searchType = "deals";

    public static boolean isShowResults = false;

    public static ImageView profilePicture;

    private GcmPreferences gcmPreferences;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private long time = 10 * 60 * 1000;

    public static TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = SharedPrefUtils.getStringVal(this, "username");
        String password = SharedPrefUtils.getStringVal(this, "password");
        String secret = SharedPrefUtils.getStringVal(this, "secret");

        HomeActivity.lat = SharedPrefUtils.getFloatValue(this, "lat");
        HomeActivity.lng = SharedPrefUtils.getFloatValue(this, "lng");

        if(!username.equals(SharedPrefUtils.EMPTY)) {
            BizStore.username = username;
        }

        if(!password.equals(SharedPrefUtils.EMPTY)) {
            BizStore.password = password;
        }

        BizStore.secret = secret;

        PROFILE_PIC_URL = BaseAsyncTask.SERVER_URL + "uploads/user/" + BizStore.username + ".jpg";

        overrideFonts();
        setContentView(R.layout.activity_home);

        CategoryUtils.setUpSubCategories(this);

        init();

        registeredWithGcmIfRequired();

        new SearchKeywordsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        tvName = (TextView) findViewById(R.id.name);

        if(!BizStore.username.isEmpty()) {
            new AccountDetailsTask(tvName, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BizStore.username);
        }

        BizStore.forceStopTasks = false;

        checkIfGpsEnabled();

        if(!BuildConfig.DEBUG && !BizStore.username.isEmpty()) {
          startSubscriptionCheck();
        }
    }

    Timer timer;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CheckSubscriptionTask subscriptionTask = new CheckSubscriptionTask(HomeActivity.this,
                            timer, timerTask);
                    subscriptionTask.execute();
                }
            });
        }
    };

    private void startSubscriptionCheck() {
        timer = new Timer();

        timer.schedule(timerTask, 0, time);
    }

    private void overrideFonts() {
        BizStore bizStore = (BizStore) getApplicationContext();
        bizStore.overrideDefaultFonts();
    }

    private LocationManager locationManager;

    int minTimeMillis = 15 * (60 * 1 * 1000);
    int distanceMeters = 50;

    @ViewById(R.id.scrollToTop)
    public FloatingActionButton fab;

    NavigationMenuUtils navigationMenuUtils;

    private void init() {

        diskCache.requestInit(this);

        gcmPreferences = new GcmPreferences(this);

        RecentSearchesAdapter.homeActivity = this;

        initFilter();

        searchView = getLayoutInflater().inflate(R.layout.search_popup, null);
        searchPopup = new PopupWindow(searchView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        searchBusinesses.setVisibility(View.GONE);

        setSearchCheckboxSelection();

        searchDeals.setOnClickListener(this);
        searchBusinesses.setOnClickListener(this);

        homePagerAdapter = new HomePagerAdapter(this, getSupportFragmentManager());

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        drawerLayout.setDrawerListener(mDrawerListener);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMillis, distanceMeters, this);
        }

        if(locationManager.getProvider(LocationManager.GPS_PROVIDER) != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMillis, distanceMeters, this);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null) {
            Logger.print("Location Changed#LastKnown: " + location.getLatitude() + ", " + location.getLongitude());

            SharedPrefUtils.updateVal(this, "lat", (float) location.getLatitude());
            SharedPrefUtils.updateVal(this, "lng", (float) location.getLongitude());
        }

        setRecentSearches();

        mPopularSearchesGridAdapter = new PopularSearchesGridAdapter(this, R.layout.list_popular_search, new ArrayList<KeywordSearch>());
        mPopularSearchGridView.setAdapter(mPopularSearchesGridAdapter);

        setSearchSuggestions(new ArrayList<String>());

        fab.setOnClickListener(this);

        setupSearchField();

        navigationMenuUtils = new NavigationMenuUtils(this, expandableListView);
        navigationMenuUtils.setupNavigationMenu();

        setupToolbar();

        setupTabs();

        setupPager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PROFILE_PIC_URL = BaseAsyncTask.SERVER_URL + "uploads/user/" + BizStore.username + ".jpg";

        navigationMenuUtils.onResume();

        // Hockey app crash checker
        if(BuildConfig.DEBUG) {
            checkForCrashes();
        }
    }

    private void checkForCrashes()
    {
        CrashManager.register(this);
    }

    public void setSearchSuggestions(List<String> list) {

        ListView mSearchSuggestionListView = (ListView) searchView.findViewById(R.id.list_search_suggestions);

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

        acSearch.setOnEditorActionListener(this);
    }

    @ViewById(R.id.appBar)
    public static AppBarLayout appBarLayout;

    @ViewById(R.id.coordinatorLayout)
    public CoordinatorLayout coordinatorLayout;

    @ViewById
    Toolbar toolbar;

    private void setupToolbar() {

        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setLogo( R.drawable.ic_bizstore);

        View logoView = getToolbarLogoIcon(toolbar);

        logoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTab(0);
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
        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);
    }

    private void setupPager() {
        viewPager.setAdapter(null);
        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new HomeTabSelectedListener(this, viewPager));
    }

    @ViewById(R.id.back)
    ImageView ivBack;

    @ViewById(R.id.done)
    TextView tvDone;

    @ViewById(R.id.rating_1)
    TextView tvRating1;

    @ViewById(R.id.rating_2)
    TextView tvRating2;

    @ViewById(R.id.rating_3)
    TextView tvRating3;

    @ViewById(R.id.rating_4)
    TextView tvRating4;

    @ViewById(R.id.rating_5)
    TextView tvRating5;

    @ViewById(R.id.cb_highest_discount)
    CheckBox cbHighestDiscount;

    @ViewById(R.id.cb_distance)
    CheckBox cbDistance;

    /**
     * Initialize the right drawer i.e filter layout
     */
    private void initFilter() {
        FilterOnClickListener clickListener = new FilterOnClickListener(this, 0);

        ivBack.setOnClickListener(clickListener);

        tvDone.setOnClickListener(clickListener);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            tvDone.setBackgroundResource(R.drawable.white_btn_ripple);
        }

        tvRating1.setOnClickListener(clickListener);
        tvRating2.setOnClickListener(clickListener);
        tvRating3.setOnClickListener(clickListener);
        tvRating4.setOnClickListener(clickListener);
        tvRating5.setOnClickListener(clickListener);


        cbHighestDiscount.setOnClickListener(clickListener);
        cbDistance.setOnClickListener(clickListener);

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

        doApplyDiscount = false;
        cbHighestDiscount.setChecked(doApplyDiscount);
        cbHighestDiscount.setText(getString(R.string.sort_discount));

        doApplyDistance = false;
        cbDistance.setChecked(doApplyDistance);

        doApplyRating = false;
        ratingFilter = null;

        distanceFilter = null;

        CategoryUtils.resetCheckboxes();
    }

    private void checkIfGpsEnabled()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            DialogUtils.createLocationDialog(this, null).show();
        }
    }

    private void registeredWithGcmIfRequired() {
        if(isGPlayServicesAvailable() && !BizStore.username.isEmpty()) {
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

    public static MenuItem miSearch, miFilter;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        mMenu = menu;

        miSearch = menu.findItem(R.id.action_search);

        miFilter = menu.findItem(R.id.action_filter);
        miFilter.setVisible(true);

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
            } else {
                showHideDrawer(GravityCompat.START, true);
            }
        } else if(id == R.id.action_search || id == R.id.search) {
            boolean show = id == R.id.action_search;

            if (id == R.id.action_search) {
                miFilter.setVisible(false);
            } else {
                if (currentFragment != null && !(currentFragment instanceof HomeFragment)) {
                    miFilter.setVisible(true);
                }
            }
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

      //  diskCache.requestFlush();
    }

    public void hideSearchResults() {
        isShowResults = false;
        llSearch.setVisibility(View.VISIBLE);
        showHideSearchBar(false);
    }

    public void showHideDrawer(int gravity, boolean show) {
        if(show) {
            drawerLayout.openDrawer(gravity);
        } else {
            drawerLayout.closeDrawer(gravity);
        }
    }

    @ViewById(R.id.grid_popular_searches)
    View popularGrid;

    @ViewById(R.id.layout_search_filter)
    View searchLayout;

    @ViewById(R.id.lv_search_results)
    View searchResultView;

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

        if(show)
        {
            showSearchPopup();

            if(fab.getVisibility() == View.VISIBLE)
            {
                AnimatorUtils.hideFab(fab);
            }

        } else {
            hideSearchPopup();
        }
    }

    @ViewById(R.id.layout_search)
    LinearLayout llSearch;

    public void showSearchPopup() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        isShowResults = false;
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        setRecentSearches();
        setPopularSearches(AppData.popularSearches.list);

        llSearch.setVisibility(View.VISIBLE);
        mRecentSearchListView.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);
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

    @ViewById(R.id.layout_popular_searches)
    LinearLayout layoutPopularSearches;

    public void setPopularSearches(List<KeywordSearch> list) {
        if(list != null) {
            mPopularSearchesGridAdapter = new PopularSearchesGridAdapter(this, R.layout.list_popular_search, list);
            mPopularSearchGridView.setAdapter(mPopularSearchesGridAdapter);

            layoutPopularSearches.setVisibility(list.size() == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public void hideSearchPopup() {
        isShowResults = true;

        llSearch.setVisibility(View.GONE);
        mSearchResultsListView.setVisibility(View.GONE);
        mRecentSearchListView.setVisibility(View.GONE);

        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void selectTab(int tabPosition) {
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

        if(!BizStore.username.isEmpty()) {
            LocationUpdateTask locationUpdateTask = new LocationUpdateTask();
            locationUpdateTask.execute(lat, lng);
        }

        // This code used to tell all the fragments that
        // the user location has been updated so that they can
        // update themselves to show updated distance and deals
        // But now by default we show the newest deals first
        // so no need any more
        /*for(int i = 0; i < HomePagerAdapter.PAGE_COUNT; i++)
        {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:"
                    +R.id.home_viewpager + ":" + i);

            if(fragment != null && !(fragment instanceof HomeFragment))
            {
                NavigationMenuOnClickListener.clearCache(this);

                ((LocationChangeListener) fragment).onLocationChanged();
            }
        }*/
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

    /**
     * This helps in determining which fragment is currently
     * user viewing and can help in interfacing calls etc
     * @param currentFragment
     */
    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    @Override
    public void onFilterChange() {
        Logger.print("HomeActivity onFilterChange Fragment: "+currentFragment.getClass().getName());

        ((OnFilterChangeListener) currentFragment).onFilterChange();
    }

    public void populateSearchResults(List<SearchResult> searchResultList) {
            mSearchResultsAdapter = new SearchBaseAdapter(this, R.layout.list_deal_promotional, searchResultList);
            mSearchResultsListView.setAdapter(mSearchResultsAdapter);
    }

    public void setupSearchResults(String keyword, List<SearchResult> results, boolean isKeywordSearch) {

        isSearchTextWatcherEnabled = false;

        if(isNotNullOrEmpty(keyword)) {
            acSearch.setText(keyword);
        }

        if(results == null)
            results = new ArrayList<>();

        hideSearchPopup();
        HomeActivity.isShowResults = true;

        populateSearchResults(results);

        layoutPopularSearches.setAlpha(MAX_ALPHA);

        mRecentSearchListView.setVisibility(View.GONE);

        llSearch.setVisibility(View.VISIBLE);
        searchResultView.setVisibility(View.VISIBLE);
        layoutPopularSearches.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);

        searchDeals.setText(getDealsCount() + " " + getString(R.string.deals));

        if(getDealsCount() < 1)
        {
            Toast.makeText(this, R.string.error_no_search_item_found, LENGTH_SHORT).show();
        }

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
                    layoutPopularSearches.setAlpha(MAX_ALPHA);
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
                        layoutPopularSearches.setAlpha(0f);
                        int offset = (int) Converter.convertDpToPixels(15);
                        searchPopup.showAsDropDown(acSearch, 0, offset);
                    }
                }
            }
        }
    }


    public void performSearch(String keyword) {
        if(hasInternetConnection(this)) {
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
                    HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().
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

        Logger.print("Home onDestroy");

        if(timer != null)
        {
            timer.cancel();
        }

        if(searchPopup != null && searchPopup.isShowing())
            searchPopup.dismiss();

        memoryCache.tearDown();

        diskCache.requestClose();

        BitmapDownloadTask.downloadingPool.clear();

        locationManager.removeUpdates(this);

        AppData.searchSuggestions = null;

        BitmapDownloadTask.downloadingPool.clear();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}