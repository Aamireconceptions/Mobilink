package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.ui.fragments.AutomotiveFragment;
import com.ooredoo.bizstore.ui.fragments.ElectronicsFragment;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;
import com.ooredoo.bizstore.ui.fragments.FoodAndDinningFragment;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.ui.fragments.HotelsAndSpasFragment;
import com.ooredoo.bizstore.ui.fragments.JewelleryFragment;
import com.ooredoo.bizstore.ui.fragments.MallsFragment;
import com.ooredoo.bizstore.ui.fragments.ShoppingFragment;
import com.ooredoo.bizstore.ui.fragments.SportsAndFitnessFragment;
import com.ooredoo.bizstore.ui.fragments.TopDealsFragment;
import com.ooredoo.bizstore.ui.fragments.TravelFragment;

/**
 * @author Babar
 * @since 11-Jun-15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter
{
    private final static int PAGE_COUNT = 12;

    public HomePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("Creating " + position);

        switch(position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return TopDealsFragment.newInstance();
            case 2:
                return FoodAndDinningFragment.newInstance();
            case 3:
                return ShoppingFragment.newInstance();
            case 4:
                return ElectronicsFragment.newInstance();
            case 5:
                return HotelsAndSpasFragment.newInstance();
            case 6:
                return MallsFragment.newInstance();
            case 7:
                return AutomotiveFragment.newInstance();
            case 8:
                return TravelFragment.newInstance();
            case 9:
                return EntertainmentFragment.newInstance();
            case 10:
                return JewelleryFragment.newInstance();
            case 11:
                return SportsAndFitnessFragment.newInstance();
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
        return AppConstant.TAB_NAMES[position];
    }
}
