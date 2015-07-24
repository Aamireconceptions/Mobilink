package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.ooredoo.bizstore.ui.fragments.SlideFragment;

/**
 * Created by Babar on 24-Jul-15.
 */
public class DemoPagerAdapter extends FragmentPagerAdapter
{
    private final static int SLIDE_COUNT = 4;

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
