package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.FeaturedFragment;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;

import java.util.List;

import static com.ooredoo.bizstore.ui.fragments.HomeFragment.startSlider;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class FeaturedStatePagerAdapter extends FragmentStatePagerAdapter
{
    public static List<GenericDeal> deals;

    public FeaturedStatePagerAdapter(FragmentManager fm, List<GenericDeal> deals)
    {
        super(fm);

        FeaturedStatePagerAdapter.deals = deals;

        startSlider(deals, HomeFragment.promoPager);
    }

    public void setData(List<GenericDeal> deals)
    {
        FeaturedStatePagerAdapter.deals = deals;
        startSlider(deals, HomeFragment.promoPager);
    }

    @Override
    public Fragment getItem(int position)
    {
        String url = deals.get(position).image.featured;
        return FeaturedFragment.newInstance(deals.get(position).id, url);
    }

    @Override
    public int getCount()
    {
        return deals.size();
    }
}
