package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;

import com.activeandroid.query.Delete;
import com.ooredoo.bizstore.AppData;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.LanguageTask;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.Locale;

import static com.ooredoo.bizstore.ui.activities.RecentViewedActivity.clearRecentItems;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.APP_LANGUAGE;
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