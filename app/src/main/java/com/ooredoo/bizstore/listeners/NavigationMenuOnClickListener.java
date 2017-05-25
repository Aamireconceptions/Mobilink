package com.ooredoo.bizstore.listeners;

import android.app.Activity;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.utils.CategoryUtils;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Babar
 * @since 01-Jul-15.
 */
public class NavigationMenuOnClickListener  {


    public static void clearCache(Activity activity) {

        for(String key : CategoryUtils.CACHE_KEYS) {
            updateVal(activity, key, "");
            updateVal(activity, key.concat("_UPDATE"), 0);
        }

        for(String category : CategoryUtils.categories) {
            final String KEY = PREFIX_DEALS.concat(category);
            final String UPDATE_KEY = KEY.concat("_UPDATE");
            updateVal(activity, KEY, "");
            updateVal(activity, UPDATE_KEY, 0);
        }
    }
}