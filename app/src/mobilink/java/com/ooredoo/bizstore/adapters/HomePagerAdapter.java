package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.HealthFragment;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;
import com.ooredoo.bizstore.ui.fragments.FoodAndDiningFragment;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.ui.fragments.NearbyFragment;
import com.ooredoo.bizstore.ui.fragments.NewArrivalsFragment;
import com.ooredoo.bizstore.ui.fragments.ShoppingFragment;
import com.ooredoo.bizstore.ui.fragments.TopDealsFragment;
import com.ooredoo.bizstore.utils.Logger;

/**
 * @author Babar
 * @since 11-Jun-15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter
{
    private Context context;

    public final static int PAGE_COUNT = 7;

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
            /*case 1:
                return NearbyFragment.newInstance();*/
            case 1:
                return TopDealsFragment.newInstance();
            case 2:
                return FoodAndDiningFragment.newInstance();
            case 3:
                return ShoppingFragment.newInstance();
            case 4:
                return HealthFragment.newInstance();
            case 5:
                return EntertainmentFragment.newInstance();
            case 6:
                return NewArrivalsFragment.newInstance();
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

            /*case 1:

                return context.getString(R.string.nearby);*/

            case 1:

                return context.getString(R.string.top_deals);

            case 2:

                return context.getString(R.string.food_dining);

            case 3:

                return context.getString(R.string.shopping);

            case 4:

                return context.getString(R.string.health_fitness);

            case 5:

                return context.getString(R.string.entertainment);

            case 6:

                return context.getString(R.string.new_arrivals);
        }

        return "No Name";
    }

}
