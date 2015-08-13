package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.FeaturedFragment;
import com.ooredoo.bizstore.utils.SliderUtils;

import java.util.List;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class FeaturedStatePagerAdapter extends FragmentStatePagerAdapter
{
    private List<GenericDeal> deals;

    private SliderUtils featuredSlider;

    public FeaturedStatePagerAdapter(FragmentManager fm, List<GenericDeal> deals,
                                     SliderUtils featuredSlider)
    {
        super(fm);

        this.deals = deals;

        this.featuredSlider = featuredSlider;

        //startSlider(deals, HomeFragment.promoPager);
    }

    public void setData(List<GenericDeal> deals)
    {
        this.deals = deals;
        //startSlider(deals, HomeFragment.promoPager);

        featuredSlider.start(getCount());
    }

    @Override
    public Fragment getItem(int position)
    {
        //String url = deals.get(position).image.featured;

        GenericDeal genericDeal = deals.get(position);

        return FeaturedFragment.newInstance(genericDeal);
    }

    @Override
    public int getCount()
    {
        return deals.size();
    }

    public void clear()
    {
        deals.clear();
    }
}
