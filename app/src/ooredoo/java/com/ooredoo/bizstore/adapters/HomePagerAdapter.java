package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.AutomotiveFragment;
import com.ooredoo.bizstore.ui.fragments.ElectronicsFragment;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;
import com.ooredoo.bizstore.ui.fragments.FoodAndDiningFragment;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.ui.fragments.HotelsAndSpasFragment;
import com.ooredoo.bizstore.ui.fragments.JewelleryFragment;
import com.ooredoo.bizstore.ui.fragments.MallsFragment;
import com.ooredoo.bizstore.ui.fragments.NearbyFragment;
import com.ooredoo.bizstore.ui.fragments.RamadanFragment;
import com.ooredoo.bizstore.ui.fragments.ShoppingFragment;
import com.ooredoo.bizstore.ui.fragments.SportsAndFitnessFragment;
import com.ooredoo.bizstore.ui.fragments.TopDealsFragment;
import com.ooredoo.bizstore.ui.fragments.TravelFragment;
import com.ooredoo.bizstore.utils.Logger;

/**
 * @author Babar
 * @since 11-Jun-15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter
{
    private Context context;

    public final static int PAGE_COUNT = 13;

    public HomePagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);

        this.context = context;
    }

    /*@Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
       // return super.getItemPosition(object);
    }*/

    @Override
    public Fragment getItem(int position) {
        Logger.print("Creating " + position);

       /* switch(position) {
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
                return ElectronicsFragment.newInstance();
            case 6:
                return HotelsAndSpasFragment.newInstance();
            case 7:
                return MallsFragment.newInstance();
            case 8:
                return AutomotiveFragment.newInstance();
            case 9:
                return TravelFragment.newInstance();
            case 10:
                return EntertainmentFragment.newInstance();
            case 11:
                return JewelleryFragment.newInstance();
            case 12:
                return SportsAndFitnessFragment.newInstance();
        }*/
        if(BizStore.getLanguage().equals("en"))
        {
            switch(position) {
                case 0:
                    return HomeFragment.newInstance();
                case 1:
                    return NearbyFragment.newInstance();
                case 2:
                    //return RamadanFragment.newInstance();
                    return TopDealsFragment.newInstance();
                case 3:
                    return FoodAndDiningFragment.newInstance();
                case 4:
                    return ShoppingFragment.newInstance();
                case 5:
                    return ElectronicsFragment.newInstance();
                case 6:
                    return HotelsAndSpasFragment.newInstance();
                case 7:
                    return MallsFragment.newInstance();
                case 8:
                    return AutomotiveFragment.newInstance();
                case 9:
                    return TravelFragment.newInstance();
                case 10:
                    return EntertainmentFragment.newInstance();
                case 11:
                    return JewelleryFragment.newInstance();
                case 12:
                    return SportsAndFitnessFragment.newInstance();
        }

        }
        else
        if(BizStore.getLanguage().equals("ar")) {
            switch (position) {
                case 12:
                    return HomeFragment.newInstance();
                case 11:
                    return NearbyFragment.newInstance();
                case 10:
                    return RamadanFragment.newInstance();
                   // return TopDealsFragment.newInstance();
                case 9:
                    return FoodAndDiningFragment.newInstance();
                case 8:
                    return ShoppingFragment.newInstance();
                case 7:
                    return ElectronicsFragment.newInstance();
                case 6:
                    return HotelsAndSpasFragment.newInstance();
                case 5:
                    return MallsFragment.newInstance();
                case 4:
                    return AutomotiveFragment.newInstance();
                case 3:
                    return TravelFragment.newInstance();
                case 2:
                    return EntertainmentFragment.newInstance();
                case 1:
                    return JewelleryFragment.newInstance();
                case 0:
                    return SportsAndFitnessFragment.newInstance();
            }
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
        /*switch (position)
        {
            case 0:

                return context.getString(R.string.home);

            case 1:

                return context.getString(R.string.nearby);

            case 2:

                return context.getString(R.string.top_deals);

            case 3:

                return context.getString(R.string.food_dining);

            case 4:

                return context.getString(R.string.shopping);

            case 5:

                return context.getString(R.string.electronics);

            case 6:

                return context.getString(R.string.hotels_spa);

            case 7:

                return context.getString(R.string.markets_malls);

            case 8:

                return context.getString(R.string.automotive);

            case 9:

                return context.getString(R.string.travel_tours);

            case 10:

                return context.getString(R.string.entertainment);

            case 11:

                return context.getString(R.string.jewelry_exchange);

            case 12:

                return context.getString(R.string.sports_fitness);
        }*/

        if(BizStore.getLanguage().equals("en"))
    {
        switch (position)
        {
            case 0:

                return context.getString(R.string.home);

            case 1:

                return context.getString(R.string.nearby);

            case 2:
                // return context.getString(R.string.ramadan_discounts);
                return context.getString(R.string.top_deals);

            case 3:

                return context.getString(R.string.food_dining);

            case 4:

                return context.getString(R.string.shopping);

            case 5:

                return context.getString(R.string.electronics);

            case 6:

                return context.getString(R.string.hotels_spa);

            case 7:

                return context.getString(R.string.markets_malls);

            case 8:

                return context.getString(R.string.automotive);

            case 9:

                return context.getString(R.string.travel_tours);

            case 10:

                return context.getString(R.string.entertainment);

            case 11:

                return context.getString(R.string.jewelry_exchange);

            case 12:

                return context.getString(R.string.sports_fitness);
        }
    }
    else
    if(BizStore.getLanguage().equals("ar")) {
        switch (position) {
            case 12:

                return context.getString(R.string.home);

            case 11:

                return context.getString(R.string.nearby);

            case 10:

                return context.getString(R.string.ramadan_discounts);
                //return context.getString(R.string.top_deals);

            case 9:

                return context.getString(R.string.food_dining);

            case 8:

                return context.getString(R.string.shopping);

            case 7:

                return context.getString(R.string.electronics);

            case 6:

                return context.getString(R.string.hotels_spa);

            case 5:

                return context.getString(R.string.markets_malls);

            case 4:

                return context.getString(R.string.automotive);

            case 3:

                return context.getString(R.string.travel_tours);

            case 2:

                return context.getString(R.string.entertainment);

            case 1:

                return context.getString(R.string.jewelry_exchange);

            case 0:

                return context.getString(R.string.sports_fitness);
        }
    }
        return "No Name";
    }
}
