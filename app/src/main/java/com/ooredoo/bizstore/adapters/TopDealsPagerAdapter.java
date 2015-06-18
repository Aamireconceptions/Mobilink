package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.ooredoo.bizstore.ui.fragments.TopDealFragment;

/**
 * @author Pehlaj Rai
 * @since 16-Jun-15.
 */
public class TopDealsPagerAdapter extends FragmentPagerAdapter {
    private final static int PAGE_COUNT = 4;

    public TopDealsPagerAdapter(AppCompatActivity activity) {
        super(activity.getFragmentManager());
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