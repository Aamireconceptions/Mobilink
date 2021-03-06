package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.ui.fragments.TopBrandFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class TopBrandsStatePagerAdapter extends FragmentStatePagerAdapter
{
    private final static float PAGE_WIDTH = 0.27f;
    public List<Brand> brands = new ArrayList<>();

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
        /*Brand brand = brands.get(position);

        return TopBrandFragment.newInstance(brand.id, brand.image.logoUrl);*/


        Brand brand = brands.get(position);

        return TopBrandFragment.newInstance(brand);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return brands.size();
    }

    @Override
    public float getPageWidth(int position)
    {
        return PAGE_WIDTH;
    }

    public void clear()
    {
        brands.clear();
    }
}
