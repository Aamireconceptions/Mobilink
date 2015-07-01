package com.ooredoo.bizstore.listeners;

import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.views.RangeSeekBar;

/**
 * Created by Babar on 01-Jul-15.
 */
public class DiscountOnSeekChangeListener implements RangeSeekBar.OnRangeSeekBarChangeListener<Integer>
{
    private HomeActivity homeActivity;

    public DiscountOnSeekChangeListener(HomeActivity homeActivity)
    {
        this.homeActivity = homeActivity;
    }


    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue)
    {
        homeActivity.minDiscount = minValue;
        homeActivity.maxDiscount = maxValue;

        Logger.print("minValue:" + minValue + ", maxValue:" + maxValue);
    }
}
