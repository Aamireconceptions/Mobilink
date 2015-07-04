package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.SubCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 1/2/2015.
 */

public class CategoryUtils {

    /* CT = CATEGORY */

    public static final int CT_TOP = 1;
    public static final int CT_FOOD = 2;
    public static final int CT_SHOPPING = 3;
    public static final int CT_ELECTRONICS = 4;
    public static final int CT_HOTELS = 5;
    public static final int CT_MALLS = 6;
    public static final int CT_AUTOMOTIVE = 7;
    public static final int CT_TRAVEL = 8;
    public static final int CT_ENTERTAINMENT = 9;
    public static final int CT_JEWELLERY = 10;
    public static final int CT_SPORTS = 11;

    public static final List<SubCategory> subCategories = new ArrayList();

    static {

        /* FOOD & DINING */
        subCategories.add(new SubCategory(1, R.id.cb_food_arabic, "Arabic", "food_arabic", CT_FOOD, false, false));
        subCategories.add(new SubCategory(2, R.id.cb_food_turkish, "Arabic", "food_turkish", CT_FOOD, false, false));
        subCategories.add(new SubCategory(3, R.id.cb_food_indian, "Arabic", "food_indian", CT_FOOD, false, false));
        subCategories.add(new SubCategory(4, R.id.cb_food_chinese, "Arabic", "food_chinese", CT_FOOD, false, false));
        subCategories.add(new SubCategory(5, R.id.cb_food_cafes, "Arabic", "food_cafes", CT_FOOD, false, false));
        subCategories.add(new SubCategory(6, R.id.cb_fast_food, "Arabic", "food_fast_food", CT_FOOD, false, false));
        subCategories.add(new SubCategory(7, R.id.cb_food_italian, "Arabic", "food_italian", CT_FOOD, false, false));
        subCategories.add(new SubCategory(8, R.id.cb_food_afghani, "Arabic", "food_afghani", CT_FOOD, false, false));
        subCategories.add(new SubCategory(9, R.id.cb_food_lebanese, "Arabic", "food_lebanese", CT_FOOD, false, false));
        subCategories.add(new SubCategory(10, R.id.cb_food_sweets, "Arabic", "food_sweets", CT_FOOD, false, false));

        /* SHOPPING */
        subCategories.add(new SubCategory(11, R.id.cb_apparel, "Arabic", "shopping_apparel", CT_SHOPPING, false, false));
        subCategories.add(new SubCategory(12, R.id.cb_home_goods, "Arabic", "shopping_home_goods", CT_SHOPPING, false, false));
        subCategories.add(new SubCategory(13, R.id.cb_sports, "Arabic", "shopping_sports_fitness", CT_SHOPPING, false, false));
        subCategories.add(new SubCategory(14, R.id.cb_shop_electronics, "Arabic", "shopping_electronics", CT_SHOPPING, false, false));
        subCategories.add(new SubCategory(15, R.id.cb_fashion, "Arabic", "shopping_fashion", CT_SHOPPING, false, false));

        /* ELECTRONICS */
        subCategories.add(new SubCategory(16, R.id.cb_tv, "Arabic", "electronics_tv_home", CT_ELECTRONICS, false, false));
        subCategories.add(new SubCategory(17, R.id.cb_computers, "Arabic", "electronics_computer_tablets", CT_ELECTRONICS, false, false));
        subCategories.add(new SubCategory(18, R.id.cb_camera_photo, "Arabic", "electronics_cameras_photography", CT_ELECTRONICS, false, false));
        subCategories.add(new SubCategory(19, R.id.cb_mobiles, "Arabic", "electronics_mobile", CT_ELECTRONICS, false, false));

        /* HOTELS & SPAS */
        subCategories.add(new SubCategory(20, R.id.cb_salons, "Arabic", "hotels_salons", CT_HOTELS, false, false));
        subCategories.add(new SubCategory(21, R.id.cb_logding, "Arabic", "hotels_lodging", CT_HOTELS, false, false));
        subCategories.add(new SubCategory(22, R.id.cb_spas, "Arabic", "hotels_spas", CT_HOTELS, false, false));

        /* MARKETS & MALLS */
        subCategories.add(new SubCategory(23, R.id.cb_hypermarkets, "Arabic", "markets_hypermarkets", CT_MALLS, false, false));
        subCategories.add(new SubCategory(24, R.id.cb_malls, "Arabic", "markets_malls", CT_MALLS, false, false));

        /* AUTOMOTIVE */
        subCategories.add(new SubCategory(25, R.id.cb_showrooms, "Arabic", "automotives_showroom", CT_AUTOMOTIVE, false, false));
        subCategories.add(new SubCategory(26, R.id.cb_auto_accessories, "Arabic", "automotives_accessories", CT_AUTOMOTIVE, false, false));
        subCategories.add(new SubCategory(27, R.id.cb_auto_services, "Arabic", "automotives_services", CT_AUTOMOTIVE, false, false));

        /* ENTERTAINMENT */
        subCategories.add(new SubCategory(28, R.id.cb_events, "Arabic", "entertainment_events", CT_ENTERTAINMENT, false, false));
        subCategories.add(new SubCategory(29, R.id.cb_kids_activities, "Arabic", "entertainment_kids_activities", CT_ENTERTAINMENT, false, false));
        subCategories.add(new SubCategory(30, R.id.cb_cinemas, "Arabic", "entertainment_cinemas", CT_ENTERTAINMENT, false, false));

        /* JEWELLERY & EXCHANGE */
        subCategories.add(new SubCategory(31, R.id.cb_gold_rate, "Arabic", "jewelry_gold", CT_JEWELLERY, false, false));
        subCategories.add(new SubCategory(32, R.id.cb_currency, "Arabic", "jewelry_currency", CT_JEWELLERY, false, false));

        /* SPORTS & FITNESS */
        subCategories.add(new SubCategory(33, R.id.cb_sports_clothing, "Arabic", "sports_clothing", CT_SPORTS, false, false));
        subCategories.add(new SubCategory(34, R.id.cb_sports_equipment, "Arabic", "sports_equipment", CT_SPORTS, false, false));
    }

    public static synchronized void showSubCategories(Activity activity, int category) {
        for(SubCategory sc : subCategories) {
            if(sc.parent == category) {
                sc.isVisible = true;
                Logger.logI("SUB CATEGORY", sc.title);
                activity.findViewById(sc.checkBoxId).setVisibility(View.VISIBLE);
            } else {
                activity.findViewById(sc.checkBoxId).setVisibility(View.GONE);
            }
        }
    }

    public static SubCategory getSubCategoryByCheckboxId(int checkBoxId) {
        for(SubCategory s : subCategories) {
            if(s.checkBoxId == checkBoxId) {
                return s;
            }
        }
        return null;
    }

    public static void updateSubCategorySelection(int checkBoxId, boolean selected) {
        Logger.logI("updateSubCategorySelection", checkBoxId + "-" + selected);
        for(SubCategory s : subCategories) {
            if(s.checkBoxId == checkBoxId) {
                Logger.logI("updateSubCategorySelection", checkBoxId + "-" + s.checkBoxId);
                s.isSelected = selected;
            }
        }
    }

    public static String getSelectedSubCategories(int category) {
        Logger.logI("getSelectedSubCategories", "---" + category);
        String selectedSubCategories = "";
        for(SubCategory s : subCategories) {
            if(s.isSelected) {
                selectedSubCategories = selectedSubCategories.concat(s.code + ",");
            }
        }
        if(selectedSubCategories.length() > 0) {
            selectedSubCategories = selectedSubCategories.substring(0, selectedSubCategories.length() - 1);
        }
        return selectedSubCategories;
    }
}
