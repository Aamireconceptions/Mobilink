package com.ooredoo.bizstore.utils;

import android.util.Log;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author  Babar
 * @since 12-Jun-15.
 */
public class Logger
{
    private static boolean enabled = true;

    public static void setEnabled(boolean enabled)
    {
       Logger.enabled = enabled;
    }

    public static void logD(String tag, String msg)
    {
        if(isNotNullOrEmpty(msg) && enabled)
        {
            Log.d(tag, msg);
        }
    }

    public static void logV(String tag, String msg)
    {
        if(isNotNullOrEmpty(msg) && enabled)
        {
            Log.v(tag, msg);
        }
    }

    public static void logE(String tag, String msg)
    {
        if(isNotNullOrEmpty(msg) && enabled)
        {
            Log.e(tag, msg);
        }
    }

    public static void logI(String tag, String msg)
    {
        if(isNotNullOrEmpty(msg) && enabled)
        {
            Log.i(tag, msg);
        }
    }

    public static void print(String msg)
    {
        if(enabled)
        {
            System.out.println(msg);
        }
    }
}
