package com.ooredoo.bizstore.listeners;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.text.Html;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.model.Home;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.nio.Buffer;

/**
 * @author Babar
 * @since 23-Jun-15.
 */
public class FilterOnClickListener implements View.OnClickListener {
    private HomeActivity activity;

    private View lastRatingSelected;

    private View lastButtonSelected, lastDistanceSelected;

    public String rating, minDiscount, maxDiscount;

    private OnFilterChangeListener onFilterChangeListener;

    private int category;

    LocationManager locationManager;

    private View layout;

    public FilterOnClickListener(HomeActivity activity, int category) {
        this.activity = activity;

        this.category = category;

        onFilterChangeListener = activity;

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    Button btList, btMap;

    public void setLayout(View layout) {
        this.layout = layout;

        Button btList = (Button) layout.findViewById(R.id.new_deals);
        btList.setText(R.string.deals);
        btList.setOnClickListener(this);
        setButtonSelected(btList);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btList);

        Button btMap = (Button) layout.findViewById(R.id.popular_deals);
        btMap.setText(category == CategoryUtils.CT_NEARBY ? R.string.map : R.string.brands);
        btMap.setOnClickListener(this);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btMap);

        Button ivFilter = (Button) layout.findViewById(R.id.filter);
        ivFilter.setOnClickListener(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ivFilter.setBackgroundResource(R.drawable.filter_ripple);
        }
    }

    static int saveCategory;

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.new_deals:

                if(category == CategoryUtils.CT_NEARBY &&
                        (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))
                {
                    return;
                }

                setButtonSelected(v);

                DealsTask.sortColumn = "createdate";
                ShoppingTask.sortColumn = "createdate";

                onFilterChangeListener.onFilterChange();

                break;

            case R.id.popular_deals:

                if(category == CategoryUtils.CT_NEARBY &&
                        (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))
                {
                    return;
                }

                setButtonSelected(v);

                DealsTask.sortColumn = "views";
                ShoppingTask.sortColumn = "views";

                onFilterChangeListener.onFilterChange();

                break;

            case R.id.filter:

               // saveCategory = category;

               // Logger.print("CAT: "+saveCategory);

                if(category == CategoryUtils.CT_NEARBY &&
                        (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))
                {
                    return;
                }

                Logger.print("FilterOnClickListener: CATEGORY -> Filter: " + String.valueOf(category));

                ((CheckBox) activity.drawerLayout.findViewById(R.id.cb_highest_discount)).setChecked(activity.doApplyDiscount);

                activity.drawerLayout.openDrawer(GravityCompat.END);

                if(category > 0) {
                    if(category == CategoryUtils.CT_NEARBY) {
                        activity.findViewById(R.id.distance_layout).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.line1).setVisibility(View.VISIBLE);
                    } else {
                        activity.findViewById(R.id.distance_layout).setVisibility(View.GONE);
                        activity.findViewById(R.id.line1).setVisibility(View.GONE);
                    }

                    TextView tvCategory = (TextView) activity.findViewById(R.id.category_selection);
                    //tvCategory.setAllCaps(true);
                    String sub = activity.getString(R.string.sub).toUpperCase();
                    String all = activity.getString(R.string.all).toUpperCase();
                    String categories = activity.getString(R.string.categories).toUpperCase();

                    if(category == CategoryUtils.CT_NEARBY || category == CategoryUtils.CT_TOP)
                    {
                        tvCategory.setText(all + " " + categories, TextView.BufferType.SPANNABLE);

                        Spannable word = (Spannable) tvCategory.getText();
                        word.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.red)),
                                0, all.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    else
                    {
                        tvCategory.setText(sub + " " + categories, TextView.BufferType.SPANNABLE);

                        Spannable word = (Spannable) tvCategory.getText();
                        word.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.red)),
                                0, sub.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                   // tvCategory.setAllCaps(true);

                    //activity.findViewById(R.id.distance_layout).setVisibility(View.GONE);
                    //activity.findViewById(R.id.line1).setVisibility(View.GONE);

                    if(category == CategoryUtils.CT_TRAVEL) {
                        //There are no sub categories in TRAVEL categories
                        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.GONE);
                    } else {
                        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.VISIBLE);
                    }
                    CategoryUtils.showSubCategories(activity, category);
                }
                break;

            case R.id.back:

                activity.drawerLayout.closeDrawer(GravityCompat.END);

                break;

            case R.id.done:

                //Logger.print("CAT: "+saveCategory);

                activity.drawerLayout.closeDrawer(GravityCompat.END);

                Logger.print("FilterOnClickListener: CATEGORY -> Apply Filter: " + String.valueOf(category));

                String subCategories = CategoryUtils.getSelectedSubCategories(category);
                DealsTask.subCategories = subCategories;
                ShoppingTask.subCategories = subCategories;
                Logger.print("SELECTION::: " + subCategories);

                onFilterChangeListener.onFilterChange();

                onFilterChangeListener.filterTagUpdate();

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

            case R.id._5:

                activity.distanceFilter = "5";

                setDistanceSelected(v);

                break;

            case R.id._10:

                activity.distanceFilter = "10";

                setDistanceSelected(v);

                break;

            case R.id._20:

                activity.distanceFilter = "20";

                setDistanceSelected(v);

                break;

            case R.id._35:

                activity.distanceFilter = "35";

                setDistanceSelected(v);

                break;

            case R.id._50:

                activity.distanceFilter = "50";

                setDistanceSelected(v);

                break;
        }
    }

    public void setButtonSelected(View v) {
        if(lastButtonSelected != null) {
            lastButtonSelected.setSelected(false);
        }

        v.setSelected(true);

        lastButtonSelected = v;
    }

    public void setRatingSelected(View v) {
        activity.setRatingEnabled(true);

        boolean isRatingEnabled = !v.isSelected();

        Logger.logI("RATING_ENABLED", v.getId() + "," + isRatingEnabled);

        if(lastRatingSelected != null) {
            lastRatingSelected.setSelected(false);
        }

        if(!isRatingEnabled) {
            activity.ratingFilter = null;
        }

        v.setSelected(isRatingEnabled);

        lastRatingSelected = v;

        if(v.isSelected())
        {
            activity.doApplyRating = true;
        }
        else
        {
            activity.doApplyRating = false;
        }
    }

    public void setDistanceSelected(View v) {
        // activity.setRatingEnabled(true);

        boolean isDistanceEnabled = !v.isSelected();

        Logger.logI("DISTANCE_ENABLED", v.getId() + "," + isDistanceEnabled);

        if(lastDistanceSelected != null) {
            lastDistanceSelected.setSelected(false);
        }

        if(!isDistanceEnabled) {
            activity.distanceFilter = null;
        }

        v.setSelected(isDistanceEnabled);

        lastDistanceSelected = v;
    }

    private void setCheckboxSelected(View v) {
        v.setSelected(!v.isSelected());
    }
}
