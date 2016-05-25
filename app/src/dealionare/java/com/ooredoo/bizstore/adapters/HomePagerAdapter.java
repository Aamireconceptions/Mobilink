package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;
import com.ooredoo.bizstore.ui.fragments.FoodAndDiningFragment;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.ui.fragments.LifestyleFragment;
import com.ooredoo.bizstore.ui.fragments.NearbyFragment;
import com.ooredoo.bizstore.ui.fragments.TopDealsFragment;
import com.ooredoo.bizstore.utils.Logger;

/**
 * @author Babar
 * @since 11-Jun-15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter
{
    private Context context;

    private final static int PAGE_COUNT = 7;

    public HomePagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Logger.print("Creating " + position);

        switch(position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return NearbyFragment.newInstance();
            case 2:
                return TopDealsFragment.newInstance();
            case 3:
                return FoodAndDiningFragment.newInstance();
            case 4:
                return ShoppingFragment.newInstance();
            case 5:
                return LifestyleFragment.newInstance();
            case 6:
                return EntertainmentFragment.newInstance();

        }

        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:

                return context.getString(R.string.home);

            case 1:

                return context.getText(R.string.nearby);

            case 2:

                return context.getText(R.string.top_deals);

            case 3:

                return context.getString(R.string.food_dining);

            case 4:

                return context.getString(R.string.shopping);

            case 5:

                return context.getString(R.string.lifestyle);

            case 6:

                return context.getString(R.string.entertainment);

        }

        return "No Name";
    }

}
