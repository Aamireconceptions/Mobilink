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

    public FilterOnClickListener(HomeActivity homeActivity)
    {
        this.homeActivity = homeActivity;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
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

        v.setSelected(!v.isSelected());
    }


}
