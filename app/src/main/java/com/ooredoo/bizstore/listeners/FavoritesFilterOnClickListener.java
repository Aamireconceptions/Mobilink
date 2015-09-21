package com.ooredoo.bizstore.listeners;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.MyFavoritesActivity;

/**
 * Created by Babar on 23-Jun-15.
 */
public class FavoritesFilterOnClickListener implements View.OnClickListener
{
    private MyFavoritesActivity activity;

    private View lastButtonSelected;

    public FavoritesFilterOnClickListener(MyFavoritesActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.deals:

                setButtonSelected(v);

                activity.isBusiness = 0;

                activity.showFavs();

                break;

            case R.id.businesses:

                setButtonSelected(v);

                activity.isBusiness = 1;

                activity.showFavs();

                break;
        }
    }

    public void setButtonSelected(View v)
    {
        if(lastButtonSelected != null)
        {
            lastButtonSelected.setSelected(false);
        }

        v.setSelected(true);

        lastButtonSelected = v;
    }
}