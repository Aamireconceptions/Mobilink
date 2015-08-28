package com.ooredoo.bizstore.listeners;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by Babar on 28-Aug-15.
 */
public class PromoOnPageChangeListener implements ViewPager.OnPageChangeListener
{
    private SwipeRefreshLayout swipeRefreshLayout;

    public PromoOnPageChangeListener(SwipeRefreshLayout swipeRefreshLayout)
    {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
        if(state != ViewPager.SCROLL_STATE_IDLE)
        {
            swipeRefreshLayout.setEnabled(false);
        }
        else
        {
            swipeRefreshLayout.setEnabled(true);
        }
    }
}
