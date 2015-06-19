package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class TopBrandsStatePagerAdapter extends FragmentStatePagerAdapter
{

    public TopBrandsStatePagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
