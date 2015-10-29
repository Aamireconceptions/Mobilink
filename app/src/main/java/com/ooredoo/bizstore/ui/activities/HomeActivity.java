package com.ooredoo.bizstore.ui.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.HomePagerAdapter;
import com.ooredoo.bizstore.adapters.PredefinedSearchesAdapter;
import com.ooredoo.bizstore.adapters.RecentSearchesAdapter;
import com.ooredoo.bizstore.adapters.SearchBaseAdapter;
import com.ooredoo.bizstore.adapters.SearchResultsAdapter;
import com.ooredoo.bizstore.asynctasks.AccountDetailsTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.CheckSubscriptionTask;
import com.ooredoo.bizstore.asynctasks.GCMRegisterTask;
import com.ooredoo.bizstore.asynctasks.SearchKeywordsTask;
import com.ooredoo.bizstore.asynctasks.SearchTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.listeners.DiscountOnSeekChangeListener;
import com.ooredoo.bizstore.listeners.DrawerChangeListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.listeners.HomeTabLayoutOnPageChangeListener;
import com.ooredoo.bizstore.listeners.HomeTabSelectedListener;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.listeners.SubCategoryChangeListener;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.Converter;
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
import static com.ooredoo.bizstore.AppData.predefinedSearches;
import static com.ooredoo.bizstore.AppData.searchResults;
import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.APP_LANGUAGE;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

public class HomeActivity extends AppCompatActivity implements OnClickListener, OnKeyListener,
                                                               OnFilterChangeListener,
                                                               TextView.OnEditorActionListener,
                                                               OnSubCategorySelectedListener,
        LocationListener{
    public static boolean rtl = false;

    public DrawerLayout drawerLayout;
    private DrawerChangeListener mDrawerListener = new DrawerChangeListener(this);

    public ListView mPredefinedSearchesListView, mSearchResultsListView;

    public PredefinedSearchesAdapter mPredefinedSearchesAdapter;

    public SearchBaseAdapter mSearchResultsAdapter;

    public PopupWindow searchPopup;
    public ActionBar mActionBar;

    Menu mMenu;
    MenuItem loaderItem;

    public AutoCompleteTextView acSearch;

    private HomePagerAdapter homePagerAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExpandableListView expandableListView;

    public boolean isSearchEnabled = false;

    public boolean doApplyDiscount = false;

    public boolean doApplyRating = true;

    public String ratingFilter;

    public int minDiscount = 0, maxDiscount = 0;

    public View searchView;

    public RangeSeekBar<Integer> rangeSeekBar;

    private TextView tvRating1, tvRating2, tvRating3, tvRating4, tvRating5;

    SubCategoryChangeListener subCategoryChangeListener;

    public static String searchType = "deals";

    public static boolean isShowResults = false;

    public CheckBox cbSearchDeals, cbSearchBusinesses;

    public static ImageView profilePicture;

    public static Dialog loader;

    private LinearLayout llTopDeals;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPrefUtils sharedPrefUtils;

    private GcmPreferences gcmPreferences;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private long time = 15 * 60 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = SharedPrefUtils.getStringVal(this, "username");
        String password = SharedPrefUtils.getStringVal(this, "password");

        if(!username.equals(SharedPrefUtils.EMPTY))
        {
            BizStore.username = username;
        }

        if(!password.equals(SharedPrefUtils.EMPTY))
        {
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

        new AccountDetailsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BizStore.username);

        BizStore.forceStopTasks = false;

       // startSubscriptionCheck();
    }

    Timer timer;

    private void startSubscriptionCheck()
    {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CheckSubscriptionTask subscriptionTask = new CheckSubscriptionTask(HomeActivity.this);
                        subscriptionTask.execute();
                    }
                });
            }
        },0, time);
    }

    private void overrideFonts()
    {
        BizStore bizStore = (BizStore) getApplicationContext();
        bizStore.overrideDefaultFonts();
    }

    LocationManager locationManager;

    int minTimeMillis = 10 * 60 * 1000;
    int distanceMeters = 50;

    private void init()
    {
        diskCache.requestInit(this);

        sharedPrefUtils = new SharedPrefUtils(this);

        gcmPreferences = new GcmPreferences(this);

        RecentSearchesAdapter.homeActivity = this;

        initFilter();

        searchView = getLayoutInflater().inflate(R.layout.search_popup, null);
        searchPopup = new PopupWindow(searchView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        findViewById(R.id.cb_search_deals).setOnClickListener(this);
        findViewById(R.id.cb_search_business).setOnClickListener(this);

        CheckBoxClickListener checkBoxClickListener = new CheckBoxClickListener();

        cbSearchDeals = (CheckBox) searchView.findViewById(R.id.cb_search_deals);
        cbSearchBusinesses = (CheckBox) searchView.findViewById(R.id.cb_search_business);

        cbSearchDeals.setOnClickListener(checkBoxClickListener);
        cbSearchBusinesses.setOnClickListener(checkBoxClickListener);

        homePagerAdapter = new HomePagerAdapter(this, getFragmentManager());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       // drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);


        drawerLayout.setDrawerListener(mDrawerListener);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMillis, distanceMeters, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null)
        {
            Logger.print("Location Changed#LastKnown: "+location.getLatitude() + ", "+location.getLongitude());

            SharedPrefUtils.updateVal(this, "lat", (float) location.getLatitude());
            SharedPrefUtils.updateVal(this, "lng", (float) location.getLongitude());
        }

        viewPager = (ViewPager) findViewById(R.id.home_viewpager);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        mSearchResultsListView = (ListView) findViewById(R.id.lv_search_results);
        mPredefinedSearchesListView = (ListView) searchView.findViewById(R.id.lv_predefined_searches);

        mPredefinedSearchesAdapter = new PredefinedSearchesAdapter(this, R.layout.predefined_search_item, null);

        acSearch = (AutoCompleteTextView) findViewById(R.id.ac_search);

       /* swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setOnRefreshListener(this);*/

        setupSearchField();

        NavigationMenuUtils navigationMenuUtils = new NavigationMenuUtils(this, expandableListView);
        navigationMenuUtils.setupNavigationMenu();

        setupToolbar();

        setupTabs();

        setupPager();
    }

    private void setupSearchField() {
        acSearch.setThreshold(1);

        acSearch.addTextChangedListener(new SearchTextWatcher());

        acSearch.setOnKeyListener(this);

        acSearch.setOnEditorActionListener(this);
    }

    public static AppBarLayout appBarLayout;
    private void setupToolbar() {

        appBarLayout = (AppBarLayout) findViewById(R.id.appBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);

        boolean isArabic = BizStore.getLanguage().equals("ar");
        mActionBar.setLogo(isArabic ? R.drawable.ic_bizstore_arabic : R.drawable.ic_bizstore);
    }

    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setOnTabSelectedListener(new HomeTabSelectedListener(this, viewPager));
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);

    }

    private void setupPager() {
        viewPager.setAdapter(homePagerAdapter);
        viewPager.addOnPageChangeListener(new HomeTabLayoutOnPageChangeListener(tabLayout));
        //viewPager.setOffscreenPageLimit(11);
    }

    private void initFilter() {
        FilterOnClickListener clickListener = new FilterOnClickListener(this, 0);

        ImageView ivBack = (ImageView) findViewById(R.id.back);
        ivBack.setOnClickListener(clickListener);

        TextView tvDone = (TextView) findViewById(R.id.done);
        tvDone.setOnClickListener(clickListener);

        /*TextView tvDealsAndDiscount = (TextView) findViewById(R.id.deals_discount_checkbox);
        tvDealsAndDiscount.setOnClickListener(clickListener);

        TextView tvBusinessAndDirectory = (TextView) findViewById(R.id.business_directory_checkbox);
        tvBusinessAndDirectory.setOnClickListener(clickListener);*/

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
        discountCheckBox.setChecked(doApplyDiscount);
        discountCheckBox.setText(getString(R.string.sort_discount));
    }

    private void registeredWithGcmIfRequired()
    {
        if(isGPlayServicesAvailable())
        {
            Logger.print("GPlayServicesAvailable");

            String deviceGcmToken = gcmPreferences.getDeviceGCMToken();

            Logger.print("deviceGcmToken: " + deviceGcmToken);

            String userGcmId = gcmPreferences.getUserGCMToken(BizStore.username + "_" + deviceGcmToken);

            Logger.print("userGcmId: "+userGcmId);

            if(userGcmId != null)
            {
                Logger.print("Skipping gcmRegistering. User already registered");
                return;
            }

            Logger.print("Performing GCM registration");

            GCMRegisterTask gcmRegisterTask = new GCMRegisterTask(this, gcmPreferences);
            gcmRegisterTask.execute();
        }
        else
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        mMenu = menu;
        loaderItem = mMenu.findItem(R.id.action_loading);
        loaderItem.setActionView(R.layout.loader);
        loaderItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {

            Logger.print("as");
            if(isSearchEnabled) {
                hideSearchResults();
            } else {
                showHideDrawer(GravityCompat.START, true);
            }
        } else if(id == R.id.action_search || id == R.id.search) {
            boolean show = id == R.id.action_search;
            if(!show) {
                isShowResults = false;
                acSearch.setText("");
                acSearch.setHint(R.string.search);
            }
            showHideSearchBar(show);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        diskCache.requestFlush();
    }

    public void hideSearchResults() {
        isShowResults = false;
        findViewById(R.id.layout_search_results).setVisibility(View.VISIBLE);
        showHideSearchBar(false);
    }

    public void showHideDrawer(int gravity, boolean show) {
        if(show) {
            drawerLayout.openDrawer(gravity);
        } else {
            drawerLayout.closeDrawer(gravity);
        }
    }

    public void showHideSearchBar(boolean show) {
        isSearchEnabled = show;
        mMenu.findItem(R.id.search).setVisible(show);
        mMenu.findItem(R.id.action_search).setVisible(!show);
        mActionBar.setDisplayUseLogoEnabled(!show);
        if(!show) {
            //acSearch.setText(null);
            findViewById(R.id.layout_search_results).setVisibility(View.GONE);
        }
        mActionBar.setHomeAsUpIndicator(show ? R.drawable.ic_action_back : R.drawable.ic_menu);
        acSearch.setVisibility(show ? View.VISIBLE : View.GONE);
		
		 acSearch.requestFocus();

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );

// Dispatch touch event to view
        acSearch.dispatchTouchEvent(motionEvent);
        if(show) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSearchPopup();
                }
            }, 100);
        } else {
            hideSearchPopup();
        }
    }

    public void showSearchPopup() {
        isShowResults = false;
        tabLayout.setAlpha(0.25f);
        viewPager.setAlpha(0.25f);
        setPredefinedSearches();
        findViewById(R.id.layout_search_results).setVisibility(View.GONE);
        searchPopup.showAsDropDown(acSearch, 10, 25);
    }

    public void setPredefinedSearches() {
        if(predefinedSearches != null && predefinedSearches.list != null) {
            mPredefinedSearchesAdapter = new PredefinedSearchesAdapter(this, R.layout.predefined_search_item, predefinedSearches.list);
            mPredefinedSearchesListView.setAdapter(mPredefinedSearchesAdapter);
            mPredefinedSearchesAdapter.notifyDataSetChanged();
            mPredefinedSearchesListView.setVisibility(View.VISIBLE);
        }
    }

    public void hideSearchPopup() {
        isShowResults = true;
        tabLayout.setAlpha(MAX_ALPHA);
        viewPager.setAlpha(MAX_ALPHA);
        searchPopup.dismiss();
        //acSearch.setText(null);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(acSearch.getWindowToken(), 0);
    }

    public void selectTab(int tabPosition) {
        viewPager.setCurrentItem(tabPosition, true);
    }

    @Override
    public void onSubCategorySelected()
    {
        ((OnSubCategorySelectedListener) currentFragment).onSubCategorySelected();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Logger.print("Location Changed: "+lat + ", "+lng);

        SharedPrefUtils.updateVal(this, "lat", (float) lat);
        SharedPrefUtils.updateVal(this, "lng", (float) lng);
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


    public class CheckBoxClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            ((CheckBox) findViewById(v.getId())).setChecked(((CheckBox) v).isChecked());
            setSearchCheckboxSelection(cbSearchDeals, cbSearchBusinesses);
        }
    }

    public void selectDealsAndBusiness() {
        searchType = "all";
        cbSearchDeals.setChecked(true);
        cbSearchBusinesses.setChecked(true);
        ((CheckBox) findViewById(R.id.cb_search_deals)).setChecked(true);
        ((CheckBox) findViewById(R.id.cb_search_business)).setChecked(true);
    }

    public void setSearchCheckboxSelection(CheckBox cbSearchDeals, CheckBox cbSearchBusinesses) {
        if(cbSearchDeals.isChecked() && cbSearchBusinesses.isChecked()) {
            searchType = "all";
        } else if(cbSearchBusinesses.isChecked()) {
            searchType = "business";
        } else {
            searchType = "deals";
            cbSearchDeals.setChecked(true);
            this.cbSearchBusinesses.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        CheckBox cbSearchDeals = (CheckBox) findViewById(R.id.cb_search_deals);
        CheckBox cbSearchBusinesses = (CheckBox) findViewById(R.id.cb_search_business);

        List<SearchResult> results;

        if(viewId == R.id.cb_search_deals || viewId == R.id.cb_search_business) {
            //((CheckBox)searchView.findViewById(v.getId())).setChecked(((CheckBox)v).isChecked());
            setSearchCheckboxSelection(cbSearchDeals, cbSearchBusinesses);
            if(searchResults != null) {
                if(searchResults.list != null & searchResults.list.size() > 0) {
                    if(searchType.equalsIgnoreCase("all")) {
                        results = searchResults.list;
                    } else if(searchType.equalsIgnoreCase("business")) {
                        results = getBusinesses();
                    } else {
                        results = getDeals();
                    }
                    String keyword = acSearch.getText().toString();
                    setupSearchResults(keyword, results, false);
                }
            }
            Logger.print("SEARCH_FILTER: " + searchType);
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

    public void showDealDetailActivity(String dealCategory, GenericDeal genericDeal)
    {
        Intent intent = new Intent();
        intent.setClass(this, DealDetailActivity.class);
        intent.putExtra("generic_deal", genericDeal);
        intent.putExtra(CATEGORY, dealCategory);
        startActivity(intent);
    }

    public void showBusinessDetailActivity( String dealCategory, Business business)
    {
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
        Logger.print("HomeActivity onFilterChange");

        ((OnFilterChangeListener) currentFragment).onFilterChange();
    }

    public void populateSearchResults(List<SearchResult> searchResultList) {
        if(searchResultList.size() > 0) {

            mSearchResultsAdapter = new SearchBaseAdapter(this, R.layout.list_deal_promotional, searchResultList);
            mSearchResultsListView.setAdapter(mSearchResultsAdapter);
            mSearchResultsAdapter.setData(searchResultList);
            mSearchResultsAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_no_data, LENGTH_SHORT).show();
            hideSearchResults();
            showHideSearchBar(true);
        }
    }

    public void setupSearchResults(String keyword, List<SearchResult> results, boolean isKeywordSearch) {

        if(results == null)
            results = new ArrayList<>();

        if(!isKeywordSearch && results.size() > 0) {
            SearchItem searchItem = new SearchItem(0, keyword, results.size());
            SearchItem.addToRecentSearches(searchItem);
        }

        hideSearchPopup();
        HomeActivity.isShowResults = true;
        findViewById(R.id.layout_search_results).setVisibility(View.VISIBLE);

        findViewById(R.id.iv_filter_results).setVisibility(View.GONE);
        TextView tvSearchResults = (TextView) findViewById(R.id.tv_search_results);
        tvSearchResults.setText(getString(R.string.showing) + " " + results.size() + " " + getString(R.string.results));
        tvSearchResults.setVisibility(View.VISIBLE);

        populateSearchResults(results);
    }

    public void executeSearchTask(String keyword) {
        //new SearchTask(this).execute(keyword);
        new SearchTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keyword);
    }

    public class SearchTextWatcher implements TextWatcher {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable edt) {
            mPredefinedSearchesAdapter.getFilter().filter(edt.toString());
            mPredefinedSearchesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int viewId = v.getId();
        if(viewId == R.id.ac_search) {
            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                performSearch((AutoCompleteTextView) v);
                return true;
            }
        }
        return false;
    }

    private void performSearch(AutoCompleteTextView v) {
        if(hasInternetConnection(this)) {
            String keyword = v.getText().toString();
            if(isNotNullOrEmpty(keyword)) {
                Logger.print("SEARCH_KEYWORD: " + keyword);
                new SearchTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keyword);
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
            performSearch((AutoCompleteTextView) v);
            return true;
        }
        return false;
    }



    private boolean isGPlayServicesAvailable()
    {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(code != ConnectionResult.SUCCESS)
        {
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

        //timer.cancel();
    }

}