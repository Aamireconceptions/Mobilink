package com.ooredoo.bizstore.listeners;

import android.app.Activity;
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
public class NavigationMenuOnClickListener implements View.OnClickListener {
    private Activity activity;

    private String lang = "en";

    private View lastSelected;

    public NavigationMenuOnClickListener(Activity activity) {
        this.activity = activity;

        lang = BizStore.getLanguage();
    }

    @Override
    public void onClick(View v) {
        Logger.print("lang:" + lang);
        switch(v.getId()) {
            case R.id.btn_lang_english:

                if(lang.equals("en"))
                    return;

                lang = "en";

                break;

            case R.id.btn_lang_arabic:

                if(lang.equals("ar"))
                    return;

                lang = "ar";

                break;
        }

        LanguageTask languageTask = new LanguageTask();

        if(lang.equals("en")) languageTask.execute("1"); else languageTask.execute("2");

        BizStore.forceStopTasks = true;

        SharedPrefUtils.updateVal(activity, APP_LANGUAGE, lang);

        BizStore.setLanguage(lang);

        setSelected(v);

        clearCache(activity);

        clearRecentItems();

       // AppData.searchSuggestions = null;

        AppData.popularSearches.list = new ArrayList<>();

        Favorite.clearFavorites();

        CategoryUtils.subCategories.clear();

        new Delete().from(SearchItem.class).execute();

        changeLocale();

    }

    public void setSelected(View v) {
        if(lastSelected != null) {
            lastSelected.setSelected(false);
        }

        v.setSelected(true);

        lastSelected = v;
    }

    private void changeLocale() {
        updateConfiguration(activity, lang);

        CategoryUtils.setUpSubCategories(activity);

        activity.recreate();
    }

    public static void updateConfiguration(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;

        Resources resources = activity.getBaseContext().getResources();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

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