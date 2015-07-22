package com.ooredoo.bizstore.ui.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.HomePagerAdapter;
import com.ooredoo.bizstore.adapters.PredefinedSearchesAdapter;
import com.ooredoo.bizstore.adapters.RecentSearchesAdapter;
import com.ooredoo.bizstore.adapters.SearchResultsAdapter;
import com.ooredoo.bizstore.asynctasks.SearchKeywordsTask;
import com.ooredoo.bizstore.asynctasks.SearchTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnRefreshListener;
import com.ooredoo.bizstore.listeners.DiscountOnSeekChangeListener;
import com.ooredoo.bizstore.listeners.DrawerChangeListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.listeners.HomeTabLayoutOnPageChangeListener;
import com.ooredoo.bizstore.listeners.HomeTabSelectedListener;
import com.ooredoo.bizstore.listeners.SubCategoryChangeListener;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.model.SearchResult;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.NavigationMenuUtils;
import com.ooredoo.bizstore.views.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.BUSINESS;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.MAX_ALPHA;
import static com.ooredoo.bizstore.AppData.predefinedSearches;
import static com.ooredoo.bizstore.AppData.searchResults;
import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

public class HomeActivity extends AppCompatActivity implements OnClickListener, OnKeyListener,
                                                               OnFilterChangeListener,
                                                               TextView.OnEditorActionListener,
                                                               SwipeRefreshLayout.OnRefreshListener {
    public static boolean rtl = false;

    public DrawerLayout drawerLayout;
    private DrawerChangeListener mDrawerListener = new DrawerChangeListener(this);

    public ListView mPredefinedSearchesListView, mSearchResultsListView;

    public PredefinedSearchesAdapter mPredefinedSearchesAdapter;

    public SearchResultsAdapter mSearchResultsAdapter;

    public PopupWindow searchPopup;
    public ActionBar mActionBar;

    Menu mMenu;

    public AutoCompleteTextView acSearch;

    private HomePagerAdapter homePagerAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExpandableListView expandableListView;
    private boolean isSearchEnabled = false;

    public boolean doApplyDiscount = false;

    public boolean doApplyRating = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.print("HomeActivity onCreate");
        setContentView(R.layout.activity_home);

        overrideFonts();

        init();

        new SearchKeywordsTask(this).execute();
    }

    private void overrideFonts()
    {
        BizStore bizStore = (BizStore) getApplicationContext();
        bizStore.overrideDefaultFonts();
    }

    private void init()
    {
        RecentSearchesAdapter.homeActivity = this;

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
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        drawerLayout.setDrawerListener(mDrawerListener);

        viewPager = (ViewPager) findViewById(R.id.home_viewpager);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        mSearchResultsListView = (ListView) findViewById(R.id.lv_search_results);
        mPredefinedSearchesListView = (ListView) searchView.findViewById(R.id.lv_predefined_searches);

        mPredefinedSearchesAdapter = new PredefinedSearchesAdapter(this, R.layout.predefined_search_item, new String[] {});

        acSearch = (AutoCompleteTextView) findViewById(R.id.ac_search);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.tool_bar, R.color.grey);
        swipeRefreshLayout.setOnRefreshListener(this);

        setupSearchField();

        NavigationMenuUtils navigationMenuUtils = new NavigationMenuUtils(this, expandableListView);
        navigationMenuUtils.setupNavigationMenu();

        setupToolbar();

        setupTabs();

        setupPager();

        initFilter();
    }

    public void setSwipeRefreshLayoutEnabled(boolean enabled)
    {
        swipeRefreshLayout.setEnabled(enabled);
    }

    @Override
    public void onRefresh()
    {
        ((OnRefreshListener) currentFragment).onRefreshStarted();
    }

    private void setupSearchField() {
        acSearch.setThreshold(1);

        acSearch.addTextChangedListener(new SearchTextWatcher());

        acSearch.setOnKeyListener(this);

        acSearch.setOnEditorActionListener(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setLogo(R.drawable.ic_bizstore);
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
    }

    private void initFilter() {
        FilterOnClickListener clickListener = new FilterOnClickListener(this, 0);

        ImageView ivBack = (ImageView) findViewById(R.id.back);
        ivBack.setOnClickListener(clickListener);

        ImageView ivDone = (ImageView) findViewById(R.id.done);
        ivDone.setOnClickListener(clickListener);

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

        rangeSeekBar = (RangeSeekBar) findViewById(R.id.discount_seekbar);
        rangeSeekBar.setEnabled(false);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new DiscountOnSeekChangeListener(this));

        subCategoryChangeListener = new SubCategoryChangeListener(this);
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
                acSearch.setHint("Search");
            }
            showHideSearchBar(show);
        }
        return super.onOptionsItemSelected(item);
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
            acSearch.setText(null);
            findViewById(R.id.layout_search_results).setVisibility(View.GONE);
        }
        mActionBar.setHomeAsUpIndicator(show ? R.drawable.ic_action_back : R.drawable.ic_menu);
        acSearch.setVisibility(show ? View.VISIBLE : View.GONE);
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
        searchPopup.showAsDropDown(acSearch, 10, 55);
    }

    public void setPredefinedSearches() {
        String[] searches = new String[] {};
        if(predefinedSearches.list != null) {
            searches = predefinedSearches.list;
        }
        mPredefinedSearchesAdapter = new PredefinedSearchesAdapter(this, R.layout.predefined_search_item, searches);
        mPredefinedSearchesListView.setAdapter(mPredefinedSearchesAdapter);
        mPredefinedSearchesAdapter.notifyDataSetChanged();
        mPredefinedSearchesListView.setVisibility(View.VISIBLE);
    }

    public void hideSearchPopup() {
        isShowResults = true;
        tabLayout.setAlpha(MAX_ALPHA);
        viewPager.setAlpha(MAX_ALPHA);
        searchPopup.dismiss();
        acSearch.setText(null);
    }

    public void selectTab(int tabPosition) {
        viewPager.setCurrentItem(tabPosition, true);
    }



    public class CheckBoxClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            ((CheckBox) findViewById(v.getId())).setChecked(((CheckBox) v).isChecked());
            setSearchCheckboxSelection(cbSearchDeals, cbSearchBusinesses);
        }
    }

    public void setSearchCheckboxSelection(CheckBox cbSearchDeals, CheckBox cbSearchBusinesses) {
        if(cbSearchDeals.isChecked() && cbSearchBusinesses.isChecked()) {
            searchType = "all";
        } else if(cbSearchBusinesses.isChecked()) {
            searchType = "business";
        } else {
            searchType = "deals";
            cbSearchDeals.setChecked(true);
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
                    populateSearchResults(results);
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

    public void showDetailActivity(int detailType, String dealCategory, int typeId) {
        Intent intent = new Intent();
        intent.setClass(this, detailType == BUSINESS ? BusinessDetailActivity.class : DealDetailActivity.class);
        intent.putExtra(AppConstant.ID, typeId);
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
        mSearchResultsAdapter = new SearchResultsAdapter(this, R.layout.search_result_item, searchResultList);
        mSearchResultsListView.setAdapter(mSearchResultsAdapter);
        mSearchResultsAdapter.setData(searchResultList);
        mSearchResultsAdapter.notifyDataSetChanged();
    }

    public void setupSearchResults(String keyword, List<SearchResult> results) {

        if(results == null)
            results = new ArrayList<>();

        if(results.size() > 0) {
            SearchItem searchItem = new SearchItem(0, keyword, results.size());
            SearchItem.addToRecentSearches(searchItem);
        }

        hideSearchPopup();
        HomeActivity.isShowResults = true;
        findViewById(R.id.layout_search_results).setVisibility(View.VISIBLE);

        findViewById(R.id.iv_filter_results).setVisibility(View.GONE);
        TextView tvSearchResults = (TextView) findViewById(R.id.tv_search_results);
        tvSearchResults.setText("Showing " + results.size() + " Results");
        tvSearchResults.setVisibility(View.VISIBLE);

        populateSearchResults(results);
    }

    public void executeSearchTask(String keyword) {
        new SearchTask(this).execute(keyword);
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
            Logger.print("SEARCH_KEYWORD: " + keyword);
            new SearchTask(this).execute(keyword);
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

    public void onRefreshCompleted()
    {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO exception
        searchPopup.dismiss();

        MemoryCache.getInstance().tearDown();

        Logger.print("HomeActivity onDestroy");
    }
}