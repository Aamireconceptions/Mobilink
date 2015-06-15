package com.ooredoo.bizstore.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by Babar on 12-Jun-15.
 */
public class Converter
{
    public static float convertDpToPixels(int dp)
    {
        Resources resources = Resources.getSystem();

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());

        Logger.print("DP: "+dp+ ", PX: "+px);

        return px;
    }
}
