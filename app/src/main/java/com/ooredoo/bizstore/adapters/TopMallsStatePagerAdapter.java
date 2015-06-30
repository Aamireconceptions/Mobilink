package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.fragments.TopBrandFragment;
import com.ooredoo.bizstore.ui.fragments.TopMallFragment;

import java.util.List;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class TopMallsStatePagerAdapter extends FragmentStatePagerAdapter
{
    private List<Mall> malls;

    private final static float PAGE_WIDTH = 0.33f;

    public TopMallsStatePagerAdapter(FragmentManager fm, List<Mall> malls)
    {
        super(fm);

        this.malls = malls;
    }

    public void setData(List<Mall> malls)
    {
        this.malls = malls;
    }

    @Override
    public Fragment getItem(int position)
    {
        //return TopMallFragment.newInstance(malls.get(position).id);

        return TopMallFragment.newInstance(1);
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