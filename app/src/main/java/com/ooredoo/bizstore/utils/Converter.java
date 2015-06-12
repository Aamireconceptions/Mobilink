package com.ooredoo.bizstore.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Babar on 12-Jun-15.
 */
public class Converter
{
    public static float convertDpToPixels(int dp)
    {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        float px = dp * (displayMetrics.densityDpi / 160f);

        return px;
    }
}
