package com.ooredoo.bizstore.ui.activities;

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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.HomePagerAdapter;
import com.ooredoo.bizstore.adapters.SuggestionsAdapter;
import com.ooredoo.bizstore.listeners.HomeTabLayoutOnPageChangeListener;
import com.ooredoo.bizstore.listeners.HomeTabSelectedListener;
import com.ooredoo.bizstore.utils.NavigationMenuUtils;

import static com.ooredoo.bizstore.adapters.SuggestionsAdapter.suggestions;


public class HomeActivity extends AppCompatActivity
{
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

        homePagerAdapter = new HomePagerAdapter(this);

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
                drawerLayout.openDrawer(GravityCompat.START);
            }
        } else if(id == R.id.action_search || id == R.id.search) {
            showHideSearchBar(id == R.id.action_search);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHideSearchBar(boolean show) {
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
            searchPopup.dismiss();
        }
    }

    private void showSearchPopup() {
        View view = getLayoutInflater().inflate(R.layout.search_popup, null);
        searchPopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSearchResultsListView = (ListView) view.findViewById(R.id.lv_search_results);
        mSuggestionsListView = (ListView) view.findViewById(R.id.lv_search_suggestions);
        mSuggestionsAdapter = new SuggestionsAdapter(this, R.layout.suggestion_list_item, suggestions);
        mSuggestionsListView.setAdapter(mSuggestionsAdapter);
        view.findViewById(R.id.tv_search_results).setVisibility(View.GONE);
        searchPopup.showAsDropDown(acSearch, 10, 55);
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