package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.ui.fragments.GalleryFragment;
import com.ooredoo.bizstore.ui.fragments.TopBrandFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class GalleryStatePagerAdapter extends FragmentStatePagerAdapter
{
    private final static float PAGE_WIDTH = 0.33f;
    public List<Gallery> galleries = new ArrayList<>();

    public GalleryStatePagerAdapter(FragmentManager fm, List<Gallery> galleries)
    {
        super(fm);

        this.galleries = galleries;
    }

    @Override
    public Fragment getItem(int position)
    {
        Gallery gallery = galleries.get(position);

        return GalleryFragment.newInstance(gallery, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return galleries.size();
    }

    @Override
    public float getPageWidth(int position)
    {
        return PAGE_WIDTH;
    }
}