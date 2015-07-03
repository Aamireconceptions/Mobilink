package com.ooredoo.bizstore.utils;

import android.content.Context;

import com.ooredoo.bizstore.R;

/**
 * @author Babar
 * @since 26-Jun-15.
 */
public class ResourceUtils
{
    public final static String FOOD_AND_DINING = "Food & Dining";

    public final static String SHOPPING = "Shopping";

    public final static String ELECTRONICS = "Electronics";

    public final static String HOTELS_AND_SPA = "Hotels & Spas";

    public final static String MALLS = "Malls";

    public final static String AUTOMOTIVE = "Automotive";

    public final static String TRAVEL_AND_TOUR = "Travel & Tours";

    public final static String ENTERTAINMENT = "Entertainment";

    public final static String JEWELLERY = "Jewellery";

    public final static String SPORTS_AND_FITNESS = "Sports & Fitness";

    public static int getDrawableResId(Context context, String category)
    {
        switch(category)
        {
            case FOOD_AND_DINING:
                return R.drawable.ic_food_dining;
            case SHOPPING:
                return R.drawable.ic_shopping;
            case ELECTRONICS:
                return R.drawable.ic_electronics;
            case HOTELS_AND_SPA:
                return R.drawable.ic_hotels;
            case MALLS:
                return R.drawable.ic_malls;
            case AUTOMOTIVE:
                return R.drawable.ic_automotive;
            case TRAVEL_AND_TOUR:
                return R.drawable.ic_travel;
            case ENTERTAINMENT:
                return R.drawable.ic_entertainment;
            case JEWELLERY:
                return R.drawable.ic_jewellery;
            case SPORTS_AND_FITNESS:
                return R.drawable.ic_sports;
        }

        return -1;
    }
}