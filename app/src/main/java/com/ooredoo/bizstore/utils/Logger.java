package com.ooredoo.bizstore.utils;

import android.util.Log;

/**
 * Created by Babar on 12-Jun-15.
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
        if(enabled)
        {
            Log.d(tag, msg);
        }
    }

    public static void logV(String tag, String msg)
    {
        if(enabled)
        {
            Log.v(tag, msg);
        }
    }

    public static void logE(String tag, String msg)
    {
        if(enabled)
        {
            Log.e(tag, msg);
        }
    }

    public static void logI(String tag, String msg)
    {
        if(enabled)
        {
            Log.i(tag, msg);
        }
    }

    public static void println(String msg)
    {
        if(enabled)
        {
            System.out.println(msg);
        }
    }
}
