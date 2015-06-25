package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.ui.fragments.PromoFragment;

/**
 * @author by Babar
 * @since 19-Jun-15.
 */
public class PromoStatePagerAdapter extends FragmentStatePagerAdapter
{

    public PromoStatePagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return PromoFragment.newInstance();
    }

    @Override
    public int getCount()
    {
        return 4;
    }
}
