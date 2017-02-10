package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.SubCategory;
import com.ooredoo.bizstore.utils.Logger;

import static com.ooredoo.bizstore.utils.CategoryUtils.getSubCategoryByCheckboxId;
import static com.ooredoo.bizstore.utils.CategoryUtils.updateSubCategorySelection;

/**
 * @author Pehlaj Rai
 * @since 04-Jul-2015.
 */

public class SubCategoryChangeListener implements CheckBox.OnCheckedChangeListener {

    public SubCategoryChangeListener(Activity activity) {
        ((CheckBox) activity.findViewById(R.id.cb_food_pakistani)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_fast_food)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_chinese)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_free_delivery)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_clothing)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_home_appliances)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_others)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_health_spas)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_health_beauty_parlors)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_health_gyms)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_health_beauty_clinics)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_cinemas)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_travel)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_events)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_kids)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_other_activities)).setOnCheckedChangeListener(this);

        //((CheckBox) activity.findViewById(R.id.cb_top_deals)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_dining)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_shopping)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_ladies)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_health)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_education)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_entertainment)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_food_dining1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_shopping1)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_health1)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_entertainment1)).setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int checkBoxId = buttonView.getId();
        SubCategory subCategory = getSubCategoryByCheckboxId(checkBoxId);
        Logger.logI("SubCategoryChangeListener: CHECK", buttonView.toString());
        Logger.logI("SubCategoryChangeListener: SUB_CATEGORY:", subCategory.parent + ":" + subCategory.title);
        updateSubCategorySelection(checkBoxId, isChecked);
    }
}