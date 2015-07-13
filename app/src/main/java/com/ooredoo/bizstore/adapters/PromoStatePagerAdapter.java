package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.PromoFragment;

import java.util.List;

/**
 * @author by Babar
 * @since 19-Jun-15.
 */
public class PromoStatePagerAdapter extends FragmentStatePagerAdapter
{
    private List<GenericDeal> deals;

    public PromoStatePagerAdapter(FragmentManager fm, List<GenericDeal> deals)
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
        String url = deals.get(position).image.promotionalUrl;
        return PromoFragment.newInstance(url, deals.get(position).id);
    }

    @Override
    public int getCount()
    {
        return deals.size();
    }
}
