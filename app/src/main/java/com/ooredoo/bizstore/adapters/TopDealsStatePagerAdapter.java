package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.ui.fragments.TopDealFragment;

/**
 * @author by Babar
 * @since 19-Jun-15.
 */
public class TopDealsStatePagerAdapter extends FragmentStatePagerAdapter
{

    public TopDealsStatePagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return TopDealFragment.newInstance();
    }

    @Override
    public int getCount()
    {
        return 4;
    }
}
