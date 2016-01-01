package com.ooredoo.bizstore.utils;

import android.view.View;

/**
 * Created by Babar on 01-Jan-16.
 */
public class AnimationUtils
{
    public static void fadeIn(View v)
    {
        v.setAlpha(0);
        v.animate().alphaBy(1).setDuration(300);
    }
}
