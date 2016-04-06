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
        ((CheckBox) activity.findViewById(R.id.cb_food_fast_food)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_restaurants)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_cafe)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_clothing)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_home_appliances)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_others)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_spas_salons)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_fitness)).setOnCheckedChangeListener(this);


        ((CheckBox) activity.findViewById(R.id.cb_cinemas)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_entertainment_others)).setOnCheckedChangeListener(this);


        //((CheckBox) activity.findViewById(R.id.cb_top_deals)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_dining)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_shopping)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_lifestyle)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_entertainment)).setOnCheckedChangeListener(this);


        ((CheckBox) activity.findViewById(R.id.cb_food_dining1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_shopping1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_lifestyle1)).setOnCheckedChangeListener(this);
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
