package com.ooredoo.bizstore.utils;

import java.util.Calendar;

/**
 * Created by Babar on 31-May-16.
 */
public class TimeUtils
{
    public static boolean isDateOver(int day, int month, int year)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        long currentTimeInMillis = System.currentTimeMillis();

        return currentTimeInMillis > calendar.getTimeInMillis();
    }

}
