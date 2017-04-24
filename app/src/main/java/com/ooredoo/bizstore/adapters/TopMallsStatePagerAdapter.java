package com.ooredoo.bizstore.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Mall;
import com.ooredoo.bizstore.ui.fragments.TopBrandFragment;
import com.ooredoo.bizstore.ui.fragments.TopMallFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.util.List;

/**
 * @author  Babar
 * @since 19-Jun-15.
 */
public class TopMallsStatePagerAdapter extends FragmentStatePagerAdapter
{
    public List<Mall> malls;

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
        Mall mall = malls.get(position);

        System.out.println("TOPMALL getItem: "+position);

        return TopMallFragment.newInstance(mall);
    }

    @Override
    public int getItemPosition(Object object) {
        Logger.print("TOPMALL getItemPosition");
        return POSITION_UNCHANGED;
    }

    @Override
    public int getCount() {
        return malls.size();
    }

    @Override
    public float getPageWidth(int position)
    {
        return PAGE_WIDTH;
    }

    public void clear()
    {
        malls.clear();
    }
}
