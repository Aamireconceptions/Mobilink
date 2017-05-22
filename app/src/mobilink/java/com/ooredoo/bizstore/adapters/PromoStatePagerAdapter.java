package com.ooredoo.bizstore.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.PromoFragment;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SliderUtils;

import java.util.List;

/**
 * @author by Babar
 * @since 19-Jun-15.
 */
public class PromoStatePagerAdapter extends FragmentStatePagerAdapter
{
    public List<GenericDeal> deals;

    private SliderUtils promoSlider;

    public PromoStatePagerAdapter(FragmentManager fm, List<GenericDeal> deals,
                                  SliderUtils promoSlider)
    {
        super(fm);

        this.deals = deals;

        this.promoSlider = promoSlider;

        //startSlider(deals, HomeFragment.promoPager);
    }

    public void setData(List<GenericDeal> deals)
    {
        this.deals = deals;
       // startSlider(deals, HomeFragment.promoPager);

        promoSlider.start(getCount());
    }

    @Override
    public Fragment getItem(int position)
    {
        //String url = deals.get(position).image.promotionalUrl;

        Logger.print("Promo getItem: "+position);
        GenericDeal genericDeal = deals.get(position);

        return PromoFragment.newInstance(genericDeal);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
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

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Yet another bug in FragmentStatePagerAdapter that destroyItem is called on fragment that hasnt been added. Need to catch
        try {
            super.destroyItem(container, position, object);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }
}
