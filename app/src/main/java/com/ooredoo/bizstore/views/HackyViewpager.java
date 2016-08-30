package com.ooredoo.bizstore.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Babar on 30-Aug-16.
 */
public class HackyViewpager extends ViewPager
{
    public HackyViewpager(Context context)
    {
        super(context);
    }

    public HackyViewpager(Context context, AttributeSet attr)
    {
        super(context, attr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();

            return false;
        }

    }
}
