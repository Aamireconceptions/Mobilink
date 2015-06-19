package com.ooredoo.bizstore.adapters;



import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.ui.fragments.HomeTopDealFragment;

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
        return HomeTopDealFragment.newInstance();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
