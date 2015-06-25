package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.ui.fragments.FeaturedFragment;

/**
 * @author by Babar
 * @since 19-Jun-15.
 */
public class FeaturedStatePagerAdapter extends FragmentStatePagerAdapter
{

    public FeaturedStatePagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return FeaturedFragment.newInstance();
    }

    @Override
    public int getCount()
    {
        return 4;
    }
}
