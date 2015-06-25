package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.FeaturedFragment;

import java.util.List;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class FeaturedStatePagerAdapter extends FragmentStatePagerAdapter
{
    private List<GenericDeal> deals;

    public FeaturedStatePagerAdapter(FragmentManager fm, List<GenericDeal> deals)
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
        return FeaturedFragment.newInstance(deals.get(position).id);
    }

    @Override
    public int getCount()
    {
        return deals.size();
    }
}
