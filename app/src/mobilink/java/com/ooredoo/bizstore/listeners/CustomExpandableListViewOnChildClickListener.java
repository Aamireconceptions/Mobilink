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
import com.ooredoo.bizstore.utils.Logger;

import static com.ooredoo.bizstore.utils.CategoryUtils.getCategoryCheckboxId;
import static com.ooredoo.bizstore.utils.CategoryUtils.getCategoryFilter;
import static com.ooredoo.bizstore.utils.CategoryUtils.getParentCategory;
import static com.ooredoo.bizstore.utils.CategoryUtils.resetSubCategories;
import static com.ooredoo.bizstore.utils.CategoryUtils.updateSubCategorySelection;

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

        String filter = getCategoryFilter(subCategory);

        int category = getParentCategory(subCategory);

        resetSubCategories(category);

        int subCategoryCheckboxId = getCategoryCheckboxId(subCategory);

        int tabPos = getTabPosition();

        Logger.print("Sub-Category: " + tabPos + ", " + subCategory);

        if(getTabPosition() != 4)
        {
            DealsTask.subCategories = filter;
        }
        else
        {
            ShoppingTask.subCategories = filter;
        }
        homeActivity.selectTab(tabPos);
        homeActivity.drawerLayout.closeDrawer(GravityCompat.START);

        updateSubCategorySelection(subCategoryCheckboxId, true);

        subCategorySelectedListener.onSubCategorySelected();

        homeActivity.filterTagUpdate();

        return true;
    }

    private int getTabPosition()
    {
        if(groupName.equals(context.getString(R.string.food_dining)))
        {
            return 3;
        }
        else
            if(groupName.equals(context.getString(R.string.shopping_speciality)))
            {
                return 4;
            }
        else
            if(groupName.equals(context.getString(R.string.health_fitness)))
            {
                return 5;
            }
        else
            if(groupName.equals(context.getString(R.string.entertainment)))
            {
                return 6;
            }

        return -1;
    }
}