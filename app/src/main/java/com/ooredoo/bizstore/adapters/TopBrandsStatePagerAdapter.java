package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.fragments.TopBrandFragment;

import java.util.List;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class TopBrandsStatePagerAdapter extends FragmentStatePagerAdapter
{
    private List<Brand> brands;

    private final static float PAGE_WIDTH = 0.33f;

    public TopBrandsStatePagerAdapter(FragmentManager fm, List<Brand> brands)
    {
        super(fm);

        this.brands = brands;
    }

    public void setData(List<Brand> brands)
    {
        this.brands = brands;
    }

    @Override
    public Fragment getItem(int position)
    {
        return TopBrandFragment.newInstance(brands.get(position).id);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public float getPageWidth(int position)
    {
        return PAGE_WIDTH;
    }
}
