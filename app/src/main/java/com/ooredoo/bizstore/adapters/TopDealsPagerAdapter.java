package com.ooredoo.bizstore.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.ui.fragments.TopDealFragment;

/**
 * @author Pehlaj Rai
 * @since 16-Jun-15.
 */
public class TopDealsPagerAdapter extends FragmentPagerAdapter {
    private final static int PAGE_COUNT = 4;

    public TopDealsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TopDealFragment.newInstance();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
