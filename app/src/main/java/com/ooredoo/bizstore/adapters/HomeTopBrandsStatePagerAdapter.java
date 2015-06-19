package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.ui.fragments.HomeTopBrandFragment;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class HomeTopBrandsStatePagerAdapter extends FragmentStatePagerAdapter
{

    private final static float PAGE_WIDTH = 0.33f;

    public HomeTopBrandsStatePagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return HomeTopBrandFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public float getPageWidth(int position)
    {
        return PAGE_WIDTH;
    }
}
