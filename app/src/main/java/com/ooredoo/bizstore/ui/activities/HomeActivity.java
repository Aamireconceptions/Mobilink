package com.ooredoo.bizstore.ui.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.HomePagerAdapter;
import com.ooredoo.bizstore.listeners.HomeTabLayoutOnPageChangeListener;
import com.ooredoo.bizstore.listeners.HomeTabSelectedListener;

public class HomeActivity extends AppCompatActivity
{
    private DrawerLayout drawerLayout;

    private HomePagerAdapter homePagerAdapter;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        init();
    }

    private void init()
    {
        homePagerAdapter = new HomePagerAdapter(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        viewPager = (ViewPager) findViewById(R.id.home_viewpager);

        setupToolbar();

        setupTabs();

        setupPager();
    }

    private void setupToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupTabs()
    {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setOnTabSelectedListener(new HomeTabSelectedListener(viewPager));
        tabLayout.addTab(tabLayout.newTab());

        // tabLayout.addTab(tabLayout.newTab());
        /*tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.food_dinning)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.shopping)));*/


        tabLayout.setTabsFromPagerAdapter(homePagerAdapter);
    }

    private void setupPager()
    {
        viewPager.setAdapter(homePagerAdapter);
        viewPager.addOnPageChangeListener(new HomeTabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:

                drawerLayout.openDrawer(GravityCompat.START);

                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
