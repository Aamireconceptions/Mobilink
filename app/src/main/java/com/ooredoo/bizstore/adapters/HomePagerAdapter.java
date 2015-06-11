package com.ooredoo.bizstore.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.FoodAndDinningFragment;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.ui.fragments.ShoppingFragment;
import com.ooredoo.bizstore.ui.fragments.TopDealsFragment;

/**
 * Created by Babar on 11-Jun-15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter
{
    private AppCompatActivity activity;

    private final static int PAGE_COUNT = 4;

    public HomePagerAdapter(AppCompatActivity activity)
    {
        super(activity.getFragmentManager());

        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0:

                return HomeFragment.newInstance();

            case 1:

                return TopDealsFragment.newInstance();

            case 2:

                return FoodAndDinningFragment.newInstance();

            case 3:

                return ShoppingFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch(position)
        {
            case 0:

                return activity.getString(R.string.home);

            case 1:

                return activity.getString(R.string.top_deals);

            case 2:

                return activity.getString(R.string.food_dinning);

            case 3:

                return activity.getString(R.string.shopping);

            default:

                return null;
        }
    }
}
