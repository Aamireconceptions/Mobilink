package com.ooredoo.bizstore.listeners;

import android.support.v4.view.GravityCompat;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.Logger;

/**
 * Created by Babar on 23-Jun-15.
 */
public class FilterOnClickListener implements View.OnClickListener
{
    private HomeActivity activity;

    private View lastRatingSelected;

    private View lastButtonSelected;

    public String rating, minDiscount, maxDiscount;

    private OnFilterChangeListener onFilterChangeListener;

    private int category;

    public FilterOnClickListener(HomeActivity activity, int category)
    {
        this.activity = activity;

        this.category = category;

        onFilterChangeListener = activity;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.new_deals:

                setButtonSelected(v);

                DealsTask.sortColumn = "createdate";
                ShoppingTask.sortColumn = "createdate";

                onFilterChangeListener.onFilterChange();

                break;

            case R.id.popular_deals:

                setButtonSelected(v);

                DealsTask.sortColumn = "views";
                ShoppingTask.sortColumn = "views";

                onFilterChangeListener.onFilterChange();

                break;

            case R.id.filter:

                activity.drawerLayout.openDrawer(GravityCompat.END);

                if(category > 0) {
                    if(category == CategoryUtils.CT_TOP || category == CategoryUtils.CT_TRAVEL) {
                        //There are no sub categories in TOP/TRAVEL categories
                        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.GONE);
                    } else {
                        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.VISIBLE);
                    }
                    Logger.logI("CATEGORY", String.valueOf(category));
                    CategoryUtils.showSubCategories(activity, category);
                }
                break;

            case R.id.back:

                activity.drawerLayout.closeDrawer(GravityCompat.END);

                break;

            case R.id.done:

                activity.drawerLayout.closeDrawer(GravityCompat.END);

                String subCategories = CategoryUtils.getSelectedSubCategories(category);
                DealsTask.subCategories = subCategories;
                ShoppingTask.subCategories = subCategories;

                Logger.logI("SELECTION", subCategories);

                onFilterChangeListener.onFilterChange();

                break;
            /*case R.id.deals_discount_checkbox:

                if(v.isSelected())
                {

                }
                else
                {

                }


                break;

            case R.id.business_directory_checkbox:


                break;*/

            case R.id.rating_checkbox:

                setCheckboxSelected(v);

                activity.doApplyRating = v.isSelected();


                activity.setRatingEnabled(v.isSelected());

                if(lastRatingSelected != null) { lastRatingSelected.setSelected(false);}

                break;

            case R.id.rating_1:

                setRatingSelected(v);

                activity.ratingFilter = "1";

                break;

            case R.id.rating_2:

                setRatingSelected(v);

                activity.ratingFilter = "2";

                break;


            case R.id.rating_3:

                setRatingSelected(v);

                activity.ratingFilter = "3";

                break;

            case R.id.rating_4:

                setRatingSelected(v);

                activity.ratingFilter = "4";

                break;

            case R.id.rating_5:

                setRatingSelected(v);

                activity.ratingFilter = "5";

                break;

            case R.id.discount_checkbox:

                setCheckboxSelected(v);

                activity.doApplyDiscount = v.isSelected();

                activity.rangeSeekBar.setEnabled(v.isSelected());

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

    public void setRatingSelected(View v)
    {
        if(lastRatingSelected != null)
        {
            lastRatingSelected.setSelected(false);
        }

        v.setSelected(true);

        lastRatingSelected = v;
    }

    private void setCheckboxSelected(View v)
    {
        v.setSelected(!v.isSelected());
    }
}
