package com.ooredoo.bizstore.utils;

import android.graphics.Color;

import com.ooredoo.bizstore.BuildConfig;

import java.util.Random;

/**
 * @author Pehlaj Rai
 * @since 1/2/2015.
 */

public class ColorUtils {
    public static final int LT_GRAY = Color.parseColor("#f2f2f2");
    public static final int RED = BuildConfig.FLAVOR.equals("ooredoo") || BuildConfig.FLAVOR.equals("mobilink")
            ? Color.parseColor("#ec1b24") : BuildConfig.FLAVOR.equals("telenor")
            ? Color.parseColor("#0091d2") : Color.parseColor("#fb9900");
    
    public static final int WHITE = Color.parseColor("#FFFFFF");
    public static final int BLACK = Color.parseColor("#000000");

    public static String getColorCode()
    {
        int min = 1;
        int max = 8;

        Random random = new Random();

        int i = random.nextInt(max - min) + min;

        Logger.print("random: "+i);

        String color = null;
        switch (i)
        {
            case 1:
                color = "#90a4ae";
                break;
            case 2:
                color = "#ff8a65";
                break;
            case 3:
                color = "#ba68c8";
                break;
            case 4:
                color = "#da4336";
                break;
            case 5:
                color = "#4fc3f7";
                break;
            case 6:
                color = "#ffa726";
                break;
            case 7:
                color = "#aed581";
                break;
            case 8:
                color = "#b39ddb";
                break;
        }

        return color;
    }
}