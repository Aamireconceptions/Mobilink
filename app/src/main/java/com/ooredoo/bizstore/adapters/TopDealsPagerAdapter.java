package com.ooredoo.bizstore.adapters;



import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.FeaturedFragment;

import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 16-Jun-15.
 */
public class TopDealsPagerAdapter extends FragmentStatePagerAdapter
{
    private List<GenericDeal> deals;

    public TopDealsPagerAdapter(FragmentManager fm, List<GenericDeal> deals)
    {
        super(fm);

        this.deals = deals;
    }

    public void setData(List<GenericDeal> deals)
    {
        this.deals = deals;
    }


    @Override
    public Fragment getItem(int position)
    {
        return FeaturedFragment.newInstance(deals.get(position).id,
                                            deals.get(position).image.featured);
    }

    @Override
    public int getCount() {
        return deals.size();
    }
}
