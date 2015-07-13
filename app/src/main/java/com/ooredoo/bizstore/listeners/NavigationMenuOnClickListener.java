package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.Logger;

import java.util.Locale;

/**
 * Created by Babar on 01-Jul-15.
 */
public class NavigationMenuOnClickListener implements View.OnClickListener
{
    private Activity activity;

    private String lang = "en";

    private View lastSelected;

    public NavigationMenuOnClickListener(Activity activity)
    {
        this.activity = activity;

        lang = BizStore.getLanguage();
    }

    @Override
    public void onClick(View v)
    {
        Logger.print("lang:" + lang);
        switch (v.getId())
        {
            case R.id.btn_lang_english:

                if(lang.equals("en")) return;

                lang = "en";

                break;

            case R.id.btn_lang_arabic:

                if(lang.equals("ar")) return;

                lang = "ar";

                break;
        }
        BizStore.setLanguage(lang);

        setSelected(v);

        changeLocale();

    }

    public void setSelected(View v)
    {
        if(lastSelected != null)
        {
            lastSelected.setSelected(false);
        }

        v.setSelected(true);

        lastSelected = v;
    }

    private void changeLocale()
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;

        activity.getBaseContext().getResources().updateConfiguration(configuration, activity
                .getBaseContext().getResources().getDisplayMetrics());

        activity.recreate();
    }
}