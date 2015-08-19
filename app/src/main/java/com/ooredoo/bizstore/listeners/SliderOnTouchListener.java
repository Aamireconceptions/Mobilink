package com.ooredoo.bizstore.listeners;

import android.view.MotionEvent;
import android.view.View;

import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SliderUtils;

/**
 * Created by Babar on 19-Aug-15.
 */
public class SliderOnTouchListener implements View.OnTouchListener
{
    private SliderUtils sliderUtils;

    public SliderOnTouchListener(SliderUtils sliderUtils)
    {
        this.sliderUtils = sliderUtils;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        Logger.print("onTouch");

        if(sliderUtils.count > 0) {
            sliderUtils.start(sliderUtils.count);
        }

        return false;
    }
}
