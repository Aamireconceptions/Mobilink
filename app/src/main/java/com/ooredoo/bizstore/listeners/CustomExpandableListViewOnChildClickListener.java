package com.ooredoo.bizstore.listeners;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.CustomExpandableListViewAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * Created by Babar on 28-Jul-15.
 */
public class CustomExpandableListViewOnChildClickListener implements ExpandableListView
                                                                     .OnChildClickListener
{
    private Context context;

    private CustomExpandableListViewAdapter adapter;

    public String groupName;

    private HomeActivity homeActivity;

    private OnSubCategorySelectedListener subCategorySelectedListener;

    public CustomExpandableListViewOnChildClickListener(Context context, CustomExpandableListViewAdapter adapter)
    {
        this.context = context;

        this.adapter = adapter;

        homeActivity = (HomeActivity) context;

        subCategorySelectedListener = homeActivity;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id)
    {
        NavigationItem navigationItem = (NavigationItem) adapter.getChild(groupPosition, childPosition);

        String subCategory = navigationItem.getItemName();

        int tabPos = getTabPosition();

        homeActivity.selectTab(tabPos);
        homeActivity.drawerLayout.closeDrawer(GravityCompat.START);

        if(getTabPosition() != 3)
        {
            DealsTask.subCategories = subCategory;
        }
        else
        {
            ShoppingTask.subCategories = subCategory;
        }

        subCategorySelectedListener.onSubCategorySelected();

        return true;
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
