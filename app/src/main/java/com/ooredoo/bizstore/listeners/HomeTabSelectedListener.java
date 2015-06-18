package com.ooredoo.bizstore.listeners;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;


/**
 * @author  Babar
 * @since 11-Jun-15.
 */
public class HomeTabSelectedListener implements TabLayout.OnTabSelectedListener
{
    private ViewPager viewPager;

    public HomeTabSelectedListener(ViewPager viewPager)
    {
        this.viewPager = viewPager;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
       System.out.println( tab.getPosition());

        viewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
