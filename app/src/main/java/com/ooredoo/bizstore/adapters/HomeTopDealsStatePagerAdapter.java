package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.ui.fragments.HomeTopDealFragment;

/**
 * @author by Babar
 * @since 19-Jun-15.
 */
public class HomeTopDealsStatePagerAdapter extends FragmentStatePagerAdapter
{

    public HomeTopDealsStatePagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return HomeTopDealFragment.newInstance();
    }

    @Override
    public int getCount()
    {
        return 4;
    }
}