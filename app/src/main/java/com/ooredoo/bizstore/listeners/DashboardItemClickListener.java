package com.ooredoo.bizstore.listeners;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;
import com.ooredoo.bizstore.ui.fragments.HotelsAndSpasFragment;

import static com.ooredoo.bizstore.utils.CategoryUtils.getCategoryCheckboxId;
import static com.ooredoo.bizstore.utils.CategoryUtils.getCategoryFilter;
import static com.ooredoo.bizstore.utils.CategoryUtils.getParentCategory;
import static com.ooredoo.bizstore.utils.CategoryUtils.resetSubCategories;
import static com.ooredoo.bizstore.utils.CategoryUtils.updateSubCategorySelection;

/**
 * @author Pehlaj Rai
 * @since 6/19/2015.
 */
public class DashboardItemClickListener implements View.OnClickListener {

    HomeActivity mActivity;

    OnFilterChangeListener onFilterChangeListener;

    public DashboardItemClickListener(HomeActivity mActivity) {
        this.mActivity = mActivity;

        onFilterChangeListener = mActivity;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(mActivity.isSearchEnabled) {
            mActivity.showHideSearchBar(false);
        } else {
            if(id == R.id.entertainment || id == R.id.shopping ||
                    id == R.id.electronics || id == R.id.restaurants) {
            /*String subCategory = id == R.id.entertainment ? "entertainment" :
                                   id == R.id.shopping ? "shopping" :
                                   id == R.id.fashion ? "fashion" :
                                                      "restaurants";*/
                switch(id) {
                    case R.id.entertainment:

                        mActivity.selectTab(9);

                        break;

                    case R.id.shopping:

                        mActivity.selectTab(3);

                        break;

                    case R.id.electronics:

                        mActivity.selectTab(4);

                        break;

                    case R.id.restaurants:

                        mActivity.selectTab(2);

                        break;
                }

            /*TopDealsFragment.subCategory = "top_deals_" + subCategory;
            mActivity.selectTab(1);*/
            }

            if(id == R.id.salons || id == R.id.lodging || id == R.id.spas) {
                String subCategory = id == R.id.salons ? "salons" : id == R.id.lodging ? "lodging" : "spas";

                HotelsAndSpasFragment.subCategory = "hotels_" + subCategory;

                processSubCategory(subCategory);

                mActivity.selectTab(5);
            }

            if(id == R.id.events || id == R.id.movie_tickets || id == R.id.kids_activities) {
                String subCategory = id == R.id.events ? "events" : id == R.id.movie_tickets ? "cinemas" : "kids_activities";
                EntertainmentFragment.subCategory = "entertainment_" + subCategory;
                String subCategoryName = getEntertainmentSubCategory(id);
                processSubCategory(subCategoryName);
                mActivity.selectTab(9);
            }

            onFilterChangeListener.onFilterChange();
        }
    }

    private String getEntertainmentSubCategory(int id) {
        return id == R.id.events ? mActivity.getString(R.string.events) : id == R.id.movie_tickets ? mActivity.getString(R.string.movie_tickets) : mActivity.getString(R.string.Kids_Activities);
    }

    private void processSubCategory(String subCategoryName) {
        String filter = getCategoryFilter(subCategoryName);

        int category = getParentCategory(subCategoryName);

        resetSubCategories(category);

        int subCategoryCheckboxId = getCategoryCheckboxId(subCategoryName);

        updateSubCategorySelection(subCategoryCheckboxId, true);

        DealsTask.subCategories = filter;
    }
}
