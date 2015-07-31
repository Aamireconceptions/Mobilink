package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.ui.fragments.SlideFragment;

/**
 * @author Babar
 * @since 24-Jul-15.
 */
public class DemoPagerAdapter extends FragmentPagerAdapter
{
    public final static int SLIDE_COUNT = 4;

    public DemoPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return SlideFragment.newInstance(position);
    }

    @Override
    public int getCount()
    {
        return SLIDE_COUNT;
    }
}
