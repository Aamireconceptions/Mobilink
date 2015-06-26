package com.ooredoo.bizstore.utils;

import android.content.Context;

import com.ooredoo.bizstore.R;

/**
 * Created by Babar on 26-Jun-15.
 */
public class ResourceUtils
{
    private final static String SHOPPING = "shopping";

    public static int getDrawableResId(Context context, String type)
    {
        switch (type)
        {
            case SHOPPING:

                return R.drawable.ic_shopping;


        }

        return -1;
    }
}
