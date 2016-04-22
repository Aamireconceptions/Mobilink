package com.ooredoo.bizstore.listeners;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

/**
 * @author  Babar
 * @since 11-Jun-15.
 */
public class HomeTabLayoutOnPageChangeListener extends TabLayout.TabLayoutOnPageChangeListener
{
    HomeActivity homeActivity;
    public HomeTabLayoutOnPageChangeListener(TabLayout tabLayout, HomeActivity homeActivity)
    {
        super(tabLayout);

        this.homeActivity = homeActivity;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        homeActivity.resetFilters();

        Logger.print("Tab Selected:" + position);

        // viewPager.setCurrentItem(tab.getPosition());

        DealsTask.sortColumn = "createdate";
        DealsTask.subCategories = "";

        ShoppingTask.sortColumn = "createdate";
        ShoppingTask.subCategories = "";

        homeActivity.doApplyDiscount = false;

        homeActivity.ratingFilter = null;

        setCurrentFragment(position);
    }

    private void setCurrentFragment(int position)
    {
        Fragment fragment = homeActivity.getFragmentManager().
                findFragmentByTag("android:switcher:" + R.id.home_viewpager + ":" + position);

        if(fragment != null)
        {
            Logger.print("frag not null: " + fragment.getClass().getName());
            homeActivity.setCurrentFragment(fragment);
        }
        else
        {
            Logger.print("frag was null");
        }
    }
}