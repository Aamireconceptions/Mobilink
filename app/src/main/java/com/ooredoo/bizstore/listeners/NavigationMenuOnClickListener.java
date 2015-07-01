package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

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
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_lang_english:

                lang = "en";

                break;

            case R.id.btn_lang_arabic:

                lang = "ar";

                break;
        }

        setSelected(v);

        changeLocale();
    }

    private void setSelected(View v)
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