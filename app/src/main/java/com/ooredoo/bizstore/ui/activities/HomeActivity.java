package com.ooredoo.bizstore.ui.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.HomePagerAdapter;
import com.ooredoo.bizstore.adapters.SearchResultsAdapter;
import com.ooredoo.bizstore.adapters.SuggestionsAdapter;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.listeners.HomeTabLayoutOnPageChangeListener;
import com.ooredoo.bizstore.listeners.HomeTabSelectedListener;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NavigationMenuUtils;
import com.ooredoo.bizstore.views.RangeSeekBar;

import static com.ooredoo.bizstore.adapters.SuggestionsAdapter.suggestions;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
                                                               RangeSeekBar.OnRangeSeekBarChangeListener
{
    public static boolean rtl = false;
    public DrawerLayout drawerLayout;
    public ListView mSuggestionsListView, mSearchResultsListView;
    public SuggestionsAdapter mSuggestionsAdapter;
    public PopupWindow searchPopup;
    Menu mMenu;
    ActionBar mActionBar;
    AutoCompleteTextView acSearch;
    private HomePagerAdapter homePagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExpandableListView expandableListView;
    private boolean isSearchEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        init();
    }

    private void init() {
        homePagerAdapter = new HomePagerAdapter(getFragmentManager());

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        viewPager = (ViewPager) findViewById(R.id.home_viewpager);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        acSearch = (AutoCompleteTextView) findViewById(R.id.ac_search);
        acSearch.setThreshold(1);

        mSuggestionsAdapter = new SuggestionsAdapter(this, R.layout.suggestion_list_item, suggestions);

        acSearch.addTextChangedListener(new SearchTextWatcher());

        NavigationMenuUtils navigationMenuUtils = new NavigationMenuUtils(this, expandableListView);
        navigationMenuUtils.setupNavigationMenu();

        setupToolbar();

        setupTabs();

        setupPager();

        initFilter();
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
        tabLayout.setOnTabSelectedListener(new HomeTabSelectedListener(viewPager));
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);
    }

    private void setupPager() {
        viewPager.setAdapter(homePagerAdapter);
        viewPager.addOnPageChangeListener(new HomeTabLayoutOnPageChangeListener(tabLayout));
    }

    private void initFilter()
    {
        FilterOnClickListener clickListener = new FilterOnClickListener();

        TextView tvDealsAndDiscount = (TextView) findViewById(R.id.deals_discount_checkbox);
        tvDealsAndDiscount.setOnClickListener(clickListener);

        TextView tvBusinessAndDirectory = (TextView) findViewById(R.id.business_directory_checkbox);
        tvBusinessAndDirectory.setOnClickListener(clickListener);

        TextView tvRating = (TextView) findViewById(R.id.rating_checkbox);
        tvRating.setOnClickListener(clickListener);

        TextView tvFoodAnDinning= (TextView) findViewById(R.id.food_dinning_checkbox);
        tvFoodAnDinning.setOnClickListener(clickListener);

        TextView tvElectronics = (TextView) findViewById(R.id.electronics_checkbox);
        tvElectronics.setOnClickListener(clickListener);

        TextView tvHotelsAndSpa = (TextView) findViewById(R.id.hotels_spa_checkbox);
        tvHotelsAndSpa.setOnClickListener(clickListener);

        RangeSeekBar<Integer> rangeSeekBar = (RangeSeekBar) findViewById(R.id.discount_seekbar);
        rangeSeekBar.setOnRangeSeekBarChangeListener(this);
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
            if(isSearchEnabled) {
                showHideSearchBar(false);
            } else {
                showHideDrawer(GravityCompat.START, true);
            }
        } else if(id == R.id.action_search || id == R.id.search) {
            showHideSearchBar(id == R.id.action_search);
        }
        return super.onOptionsItemSelected(item);
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
        tabLayout.setAlpha(0.45f);
        viewPager.setAlpha(0.45f);
        View view = getLayoutInflater().inflate(R.layout.search_popup, null);
        searchPopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSearchResultsListView = (ListView) view.findViewById(R.id.lv_search_results);
        mSuggestionsListView = (ListView) view.findViewById(R.id.lv_search_suggestions);
        mSuggestionsAdapter = new SuggestionsAdapter(this, R.layout.suggestion_list_item, suggestions);
        mSuggestionsListView.setAdapter(mSuggestionsAdapter);
        view.findViewById(R.id.cb_search_deals).setOnClickListener(this);
        view.findViewById(R.id.cb_search_business).setOnClickListener(this);
        view.findViewById(R.id.tv_search_results).setVisibility(View.GONE);
        searchPopup.showAsDropDown(acSearch, 10, 55);
    }

    public void hideSearchPopup() {
        tabLayout.setAlpha(1f);
        viewPager.setAlpha(1f);
        searchPopup.dismiss();
    }

    public void hideTabs() {
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
    }

    public void showTabs() {
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    public void showFragment(Fragment fragment) {
        hideTabs();
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "").commit();
        //replaceFragmentWithBackStack(this, R.id.fragment_container, fragment, "DEAL_DETAIL");
    }

    public void selectTab(int tabPosition) {
        viewPager.setCurrentItem(tabPosition);
    }

    @Override
    public void onClick(View v) {
        CheckBox cbSearchDeals = (CheckBox) searchPopup.getContentView().findViewById(R.id.cb_search_deals);
        CheckBox cbSearchBusiness = (CheckBox) searchPopup.getContentView().findViewById(R.id.cb_search_deals);
        if(cbSearchDeals.isChecked() && cbSearchBusiness.isChecked()) {
            SearchResultsAdapter.searchType = 3;
        } else if(cbSearchBusiness.isChecked()) {
            SearchResultsAdapter.searchType = 2;
        } else if(cbSearchDeals.isChecked()) {
            SearchResultsAdapter.searchType = 1;
        }
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue)
    {

        Logger.print("minValue:" + minValue + ", maxValue:"+maxValue);

    }

    public class SearchTextWatcher implements TextWatcher {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable edt) {
            mSuggestionsAdapter.getFilter().filter(edt.toString());
            mSuggestionsAdapter.notifyDataSetChanged();
        }
    }
}