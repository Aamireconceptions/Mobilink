package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.Gallery;
import com.ooredoo.bizstore.ui.fragments.GalleryFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class GalleryStatePagerAdapter extends FragmentStatePagerAdapter implements Serializable
{
    private boolean clickable;

    private float PAGE_WIDTH = 0.38f;
    public List<Gallery> galleries = new ArrayList<>();

    public GalleryStatePagerAdapter(FragmentManager fm, List<Gallery> galleries, boolean clickable)
    {
        super(fm);

        this.galleries = galleries;

        this.clickable = clickable;

        if(!clickable)
        {
            PAGE_WIDTH = 1f;
        }
        else
        {
            PAGE_WIDTH = 0.38f;
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        Logger.print("GalleryPagerAdapter: "+position);
        Gallery gallery = galleries.get(position);

        return GalleryFragment.newInstance(gallery, position, clickable);
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