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
        ((CheckBox) activity.findViewById(R.id.cb_food_arabic)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_turkish)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_indian)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_chinese)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_cafes)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_fast_food)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_italian)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_afghani)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_lebanese)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_sweets)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_apparel)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_home_goods)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_shop_electronics)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_fashion)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_tv)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_computers)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_camera_photo)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_mobiles)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_salons)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_logding)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_spas)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_hypermarkets)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_malls)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_showrooms)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_auto_accessories)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_auto_services)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_events)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_kids_activities)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_cinemas)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_gold_rate)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_currency)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports_clothing)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports_equipment)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports_fitness_programs)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports_kids_programs)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports_diet_programs)).setOnCheckedChangeListener(this);

        //((CheckBox) activity.findViewById(R.id.cb_top_deals)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_food_dining)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_shopping)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_electronics)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_hotels_spa)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_markets_malls)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_automotive)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_travel_tours)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_entertainment)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_jewelry)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports_fitness)).setOnCheckedChangeListener(this);

        ((CheckBox) activity.findViewById(R.id.cb_food_dining1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_shopping1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_electronics1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_hotels_spa1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_markets_malls1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_automotive1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_travel_tours1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_entertainment1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_jewelry1)).setOnCheckedChangeListener(this);
        ((CheckBox) activity.findViewById(R.id.cb_sports_fitness1)).setOnCheckedChangeListener(this);
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
