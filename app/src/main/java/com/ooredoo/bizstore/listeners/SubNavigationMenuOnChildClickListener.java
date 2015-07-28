package com.ooredoo.bizstore.listeners;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.CategoryUtils;

/**
 * Created by Babar on 28-Jul-15.
 */
public class SubNavigationMenuOnChildClickListener implements ExpandableListView
                                                                     .OnChildClickListener
{
    private Context context;

    public String groupName;

    public SubNavigationMenuOnChildClickListener(Context context)
    {
        this.context = context;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id)
    {

        return false;
    }

    private int getTabPosition()
    {
        if(groupName.equals(context.getString(R.string.food_dining)))
        {
            return 2;
        }
        else
            if(groupName.equals(context.getString(R.string.shopping_speciality)))
            {
                return 3;
            }
        else
            if(groupName.equals(context.getString(R.string.electronics)))
            {
                return 4;
            }
        else
            if(groupName.equals(context.getString(R.string.hotels_spa)))
            {
                return 5;
            }
        else
            if(groupName.equals(context.getString(R.string.markets_malls)))
            {
                return 6;
            }
        else
            if(groupName.equals(context.getString(R.string.automotive)))
            {
                return 7;
            }
        else
            if(groupName.equals(context.getString(R.string.travel_tours)))
            {
                return 8;
            }
        else
            if(groupName.equals(context.getString(R.string.entertainment)))
            {
                return 9;
            }
        else
            if(groupName.equals(context.getString(R.string.jewelry_exchange)))
            {
                return 10;
            }
        else
            if(groupName.equals(context.getString(R.string.sports_fitness)))
            {
                return 11;
            }

        return -1;
    }
}
