package com.ooredoo.bizstore.listeners;

import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.android.gms.maps.MapFragment;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;

/**
 * Created by Babar on 23-Jun-15.
 */
public class NearbyFilterOnClickListener implements View.OnClickListener
{
    private HomeActivity activity;

    private View lastRatingSelected;

    private View lastButtonSelected;

    public String rating, minDiscount, maxDiscount;

    private OnFilterChangeListener onFilterChangeListener;

    private int category;

    private View layout;

    private  ListView listView; MapFragment mapFragment;
    public NearbyFilterOnClickListener(HomeActivity activity, int category, ListView listView, MapFragment mapFragment)
    {
        this.activity = activity;

        this.category = category;

        onFilterChangeListener = activity;

        this.listView = listView;

        this.mapFragment = mapFragment;
    }
    Button btList, btMap;

    public void setLayout(View layout) {
        this.layout = layout;

        Button btList = (Button) layout.findViewById(R.id.new_deals);
        btList.setText(R.string.list);
        btList.setOnClickListener(this);
        setButtonSelected(btList);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btList);

        Button btMap = (Button) layout.findViewById(R.id.popular_deals);
        btMap.setText(R.string.map);
        btMap.setOnClickListener(this);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btMap);

        Button ivFilter = (Button) layout.findViewById(R.id.filter);
        ivFilter.setOnClickListener(this);
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

               // listView.setVisibility(View.VISIBLE);
               // mapFragment.getView().setVisibility(View.GONE);
                break;

            case R.id.popular_deals:

                setButtonSelected(v);

                DealsTask.sortColumn = "views";
                ShoppingTask.sortColumn = "views";

                onFilterChangeListener.onFilterChange();

                //listView.setVisibility(View.GONE);
               // mapFragment.getView().setVisibility(View.VISIBLE);

                break;

            case R.id.filter:

                activity.findViewById(R.id.distance_layout).setVisibility(View.VISIBLE);
                ((CheckBox) activity.drawerLayout.findViewById(R.id.cb_highest_discount)).setChecked(activity.doApplyDiscount);

                activity.drawerLayout.openDrawer(GravityCompat.END);

                if(category > 0) {
                    if(category == CategoryUtils.CT_TRAVEL) {
                        //There are no sub categories in TOP/TRAVEL categories
                        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.GONE);
                    } else {
                        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.VISIBLE);
                    }
                    Logger.print("CATEGORY:"+ String.valueOf(category));
                    CategoryUtils.showSubCategories(activity, category);
                }
                break;

            case R.id.back:

                activity.drawerLayout.closeDrawer(GravityCompat.END);

                break;

            case R.id.done:

                activity.drawerLayout.closeDrawer(GravityCompat.END);
                activity.findViewById(R.id.distance_layout).setVisibility(View.GONE);

                DealsTask.isMultipleCategoriesFilter = true;
                DealsTask.categories = CategoryUtils.getSelectedSubCategories(CategoryUtils.CT_NEARBY);
                Logger.print("SELECTION:: " + DealsTask.categories);

                onFilterChangeListener.onFilterChange();

                break;

            case R.id.rating_checkbox:

                setCheckboxSelected(v);

                activity.doApplyRating = true;//v.isSelected();

                activity.setRatingEnabled(v.isSelected());

                if(lastRatingSelected != null) { lastRatingSelected.setSelected(false);}

                break;

            case R.id.rating_1:

                activity.ratingFilter = "1";

                setRatingSelected(v);

                break;

            case R.id.rating_2:

                activity.ratingFilter = "2";

                setRatingSelected(v);

                break;


            case R.id.rating_3:

                activity.ratingFilter = "3";

                setRatingSelected(v);

                break;

            case R.id.rating_4:

                activity.ratingFilter = "4";

                setRatingSelected(v);

                break;

            case R.id.rating_5:

                activity.ratingFilter = "5";

                setRatingSelected(v);

                break;

            case R.id.discount_checkbox:

                setCheckboxSelected(v);

                activity.doApplyDiscount = v.isSelected();

                activity.rangeSeekBar.setEnabled(v.isSelected());

                break;

            case R.id.cb_highest_discount:

                activity.doApplyDiscount = ((CheckBox) v).isChecked();
                Logger.logI("DISCOUNT_FILTER", String.valueOf(activity.doApplyDiscount));

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
        activity.setRatingEnabled(true);

        boolean isRatingEnabled = !v.isSelected();

        Logger.logI("RATING_ENABLED", v.getId() + "," + isRatingEnabled);

        if(lastRatingSelected != null)
        {
            lastRatingSelected.setSelected(false);
        }

        if(!isRatingEnabled) {
            activity.ratingFilter = null;
        }

        v.setSelected(isRatingEnabled);

        lastRatingSelected = v;
    }

    private void setCheckboxSelected(View v)
    {
        v.setSelected(!v.isSelected());
    }
}
