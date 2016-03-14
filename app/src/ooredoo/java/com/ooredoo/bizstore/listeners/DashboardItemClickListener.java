package com.ooredoo.bizstore.listeners;

import android.os.Build;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.ElectronicsFragment;
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

            if(id == R.id.restaurants_layout || id == R.id.shopping_layout ||
                    id == R.id.health_layout || id == R.id.travel_layout ||
                    id == R.id.electronics_layout || id == R.id.automobile_layout) {
            /*String subCategory = id == R.id.entertainment ? "entertainment" :
                                   id == R.id.shopping ? "shopping" :
                                   id == R.id.fashion ? "fashion" :
                                                      "restaurants";*/
                switch(id) {
                    case R.id.restaurants_layout:

                        mActivity.selectTab(3);

                        break;

                    case R.id.shopping_layout:

                        mActivity.selectTab(4);

                        break;

                    case R.id.health_layout:

                        mActivity.selectTab(6);

                        break;

                    case R.id.travel_layout:

                        mActivity.selectTab(9);

                        break;

                    case R.id.electronics_layout:

                        mActivity.selectTab(5);

                        break;

                    case R.id.automobile_layout:

                        mActivity.selectTab(8);


                        break;


                }

            /*TopDealsFragment.subCategory = "top_deals_" + subCategory;
            mActivity.selectTab(1);*/
            }

            if(id == R.id.search_layout)
            {
                mActivity.clickSearch();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    mActivity.resetToolBarPosition();
                }

                return;
            }

            if(id == R.id.nearby_layout)
            {
                mActivity.selectTab(1);
                return;
            }


            if(id == R.id.salons || id == R.id.salons || id == R.id.salons) {
                String subCategory = id == R.id.salons ? "salons" : id == R.id.salons ? "lodging" : "spas";

                HotelsAndSpasFragment.subCategory = "hotels_" + subCategory;
                String subCategoryName = getHotelsSubCategory(id);
                mActivity.selectTab(6);

                processSubCategory(subCategoryName);
            }

            if(id == R.id.mobile_layout || id == R.id.jewellery_layout || id == R.id.sports_layout
                    || id == R.id.salons_layout || id == R.id.cinemas_layout || id == R.id.gold_layout)
            {
                String subCategory, subCategoryName;

                if(id == R.id.mobile_layout)
                {
                    subCategory = "mobile";

                    ElectronicsFragment.subCategory = "electronics_"+subCategory;

                    subCategoryName = mActivity.getString(R.string.Mobile_Phones_Accessories);

                    mActivity.selectTab(5);

                    processSubCategory(subCategoryName);
                }

                if(id == R.id.jewellery_layout)
                {
                    mActivity.selectTab(11);
                }

                if(id == R.id.sports_layout)
                {
                    mActivity.selectTab(12);

                    return;
                }

                if(id == R.id.salons_layout)
                {
                    subCategory = "salons";

                    HotelsAndSpasFragment.subCategory = "hotels_"+subCategory;

                    subCategoryName = mActivity.getString(R.string.Salons);

                    mActivity.selectTab(6);

                    processSubCategory(subCategoryName);

                    return;
                }

                if(id == R.id.cinemas_layout)
                {
                    subCategory = "cinemas";

                    HotelsAndSpasFragment.subCategory = "hotels_"+subCategory;

                    subCategoryName = mActivity.getString(R.string.Cinemas);

                    mActivity.selectTab(10);

                    processSubCategory(subCategoryName);

                    return;
                }

                if(id == R.id.gold_layout)
                {
                    subCategory = "gold";

                    HotelsAndSpasFragment.subCategory = "jewelry_"+subCategory;

                    subCategoryName = mActivity.getString(R.string.Gold_Rate);

                    mActivity.selectTab(11);

                    processSubCategory(subCategoryName);

                    return;
                }
            }

            /*if(id == R.id.events || id == R.id.movie_tickets || id == R.id.kids_activities) {
                String subCategory = id == R.id.events ? "events" : id == R.id.movie_tickets ? "cinemas" : "kids_activities";
                EntertainmentFragment.subCategory = "entertainment_" + subCategory;
                String subCategoryName = getEntertainmentSubCategory(id);

                mActivity.selectTab(10);

                processSubCategory(subCategoryName);
            }*/

            //onFilterChangeListener.onFilterChange();
        }
    }

    private String getHotelsSubCategory(int id) {
        return id == R.id.salons ? mActivity.getString(R.string.Salons) : id == R.id.salons ? mActivity.getString(R.string.Lodging) : mActivity.getString(R.string.Spas);
    }

  /*  private String getEntertainmentSubCategory(int id) {
        return id == R.id.events ? mActivity.getString(R.string.events) : id == R.id.movie_tickets ? mActivity.getString(R.string.cinemas) : mActivity.getString(R.string.Kids_Activities);
    }*/

    private void processSubCategory(String subCategoryName) {
        String filter = getCategoryFilter(subCategoryName);

        int category = getParentCategory(subCategoryName);

        resetSubCategories(category);

        int subCategoryCheckboxId = getCategoryCheckboxId(subCategoryName);

        updateSubCategorySelection(subCategoryCheckboxId, true);

        DealsTask.subCategories = filter;
    }
}
