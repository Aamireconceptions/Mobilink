package com.ooredoo.bizstore.listeners;

import android.support.v4.view.GravityCompat;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * Created by Babar on 23-Jun-15.
 */
public class FilterOnClickListener implements View.OnClickListener
{
    private HomeActivity homeActivity;

    private View lastSelected;

    public FilterOnClickListener(HomeActivity homeActivity)
    {
        this.homeActivity = homeActivity;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.new_deals:

                setSelected(v);

                break;

            case R.id.popular_deals:

                setSelected(v);

                break;

            case R.id.filter:

                homeActivity.drawerLayout.openDrawer(GravityCompat.END);

                break;

            case R.id.back:

                homeActivity.drawerLayout.closeDrawer(GravityCompat.END);

                break;

            case R.id.done:

                homeActivity.drawerLayout.closeDrawer(GravityCompat.END);

                break;
            case R.id.deals_discount_checkbox:

                if(v.isSelected())
                {

                }
                else
                {

                }


                break;

            case R.id.business_directory_checkbox:


                break;
        }
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
}
