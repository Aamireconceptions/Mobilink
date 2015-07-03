package com.ooredoo.bizstore.utils;

import android.content.Context;

import com.ooredoo.bizstore.R;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Babar
 * @since 26-Jun-15.
 */
public class ResourceUtils {

    public static int getDrawableResId(Context context, String type) {

        if(isNotNullOrEmpty(type)) {
            switch(type) {
                case "Shopping":
                    return R.drawable.ic_shopping;
                case "Automotive":
                    return R.drawable.ic_automotive;
                case "Travel & Tours":
                    return R.drawable.ic_travel;
                case "Jewellery":
                    return R.drawable.ic_jewelry;
                case "Malls":
                    return R.drawable.ic_malls;
                case "Entertainment":
                    return R.drawable.ic_entertainment;
                case "Electronics":
                    return R.drawable.ic_electronics;
                case "Sports & Fitness":
                    return R.drawable.ic_sports;
                case "Hotels & Spas":
                    return R.drawable.ic_hotels;
                case "Food & Dining":
                    return R.drawable.ic_food_dining;
            }
        }

        return 0;
    }
}
