package com.ooredoo.bizstore.listeners;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;


/**
 * @author  Babar
 * @since 11-Jun-15.
 */
public class HomeTabSelectedListener extends TabLayout.ViewPagerOnTabSelectedListener
{
    private HomeActivity homeActivity;

    private ViewPager viewPager;

    public HomeTabSelectedListener(HomeActivity homeActivity, ViewPager viewPager)
    {
        super(viewPager);
        this.homeActivity = homeActivity;

        this.viewPager = viewPager;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
       super.onTabSelected(tab);

        homeActivity.resetFilters();

       Logger.print("Tab Selected:" + tab.getPosition());

       // viewPager.setCurrentItem(tab.getPosition());

        DealsTask.sortColumn = "createdate";
        DealsTask.subCategories = "";

        ShoppingTask.sortColumn = "createdate";
        ShoppingTask.subCategories = "";

        homeActivity.doApplyDiscount = false;

        homeActivity.ratingFilter = null;

        homeActivity.filterTagUpdate();

        setCurrentFragment();
    }

    private void setCurrentFragment()
    {
        Fragment fragment = homeActivity.getFragmentManager().
                            findFragmentByTag("android:switcher:" + R.id.home_viewpager + ":" + viewPager.getCurrentItem());

        if(fragment != null)
        {
            Logger.print("frag not null: "+fragment.getClass().getName());
            homeActivity.setCurrentFragment(fragment);
        }
        else
        {
            Logger.print("frag was null");
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        DealsTask.sortColumn = "createdate";
        ShoppingTask.sortColumn = "createdate";
        //DealsTask.subCategories = "";
        //NearbyFragment.categories = "";
        //DealsTask.categories = "";
        //NearbyFragment.isMultipleCategoriesFilter = false;
        //DealsTask.isMultipleCategoriesFilter = false;

       // homeActivity.resetFilters();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
