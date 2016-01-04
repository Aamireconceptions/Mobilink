package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.ui.fragments.SlideFragment;

/**
 * @author Babar
 * @since 24-Jul-15.
 */
public class DemoPagerAdapter extends FragmentPagerAdapter
{
    public final static int SLIDE_COUNT = 3;

    public DemoPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return BizStore.getLanguage().equals("en") ? SlideFragment.newInstance(position)
                : SlideFragment.newInstance((SLIDE_COUNT - 1) - position);
    }

    @Override
    public int getCount()
    {
        return SLIDE_COUNT;
    }
}
