package com.ooredoo.bizstore.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.ui.fragments.TopBrandFragment;

/**
 * @author Pehlaj Rai
 * @since 16-Jun-15.
 */
public class TopBrandsPagerAdapter extends FragmentPagerAdapter {
    private final static int PAGE_COUNT = 4;
    private final static float PAGE_WIDTH = 33.33f;

    public TopBrandsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TopBrandFragment.newInstance();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public float getPageWidth(int position) {
        return PAGE_WIDTH;
    }
}
