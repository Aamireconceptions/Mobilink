package com.ooredoo.bizstore.listeners;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;


/**
 * @author  Babar
 * @since 11-Jun-15.
 */
public class HomeTabSelectedListener implements TabLayout.OnTabSelectedListener
{
    private HomeActivity homeActivity;

    private ViewPager viewPager;

    public HomeTabSelectedListener(HomeActivity homeActivity, ViewPager viewPager)
    {
        this.homeActivity = homeActivity;

        this.viewPager = viewPager;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
       Logger.print("Tab Selected:" + tab.getPosition());

        viewPager.setCurrentItem(tab.getPosition(), true);

        DealsTask.sortColumn = "createdate";
        DealsTask.subCategories = "";

        homeActivity.doApplyDiscount = false;

        homeActivity.ratingFilter = null;

        setCurrentFragment();
    }

    private void setCurrentFragment()
    {
        Fragment fragment = homeActivity.getFragmentManager().
                            findFragmentByTag("android:switcher:" + R.id.home_viewpager + ":" + viewPager.getCurrentItem());

        if(fragment != null)
        {
            Logger.print("frag not null: "+fragment);
            homeActivity.setCurrentFragment(fragment);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //DealsTask.sortColumn = "createdate";
        //DealsTask.subCategories = "";
        homeActivity.resetFilters();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
