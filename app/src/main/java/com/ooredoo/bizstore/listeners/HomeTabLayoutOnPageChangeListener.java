package com.ooredoo.bizstore.listeners;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author  Babar
 * @since 11-Jun-15.
 */
public class HomeTabLayoutOnPageChangeListener extends TabLayout.TabLayoutOnPageChangeListener
{
    private HomeActivity homeActivity;

    public HomeTabLayoutOnPageChangeListener(HomeActivity homeActivity, TabLayout tabLayout)
    {
        super(tabLayout);

        this.homeActivity = homeActivity;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);

        homeActivity.setSwipeRefreshLayoutEnabled(state == ViewPager.SCROLL_STATE_IDLE);
    }
}
