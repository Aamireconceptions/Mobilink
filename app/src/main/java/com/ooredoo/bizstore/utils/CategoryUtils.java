package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.SubCategory;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

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

    public static final int CT_NEARBY = 12;

    public static final String[] CACHE_KEYS = { "TOP_BRANDS", "TOP_MALLS", "PROMO_DEALS", "FEATURED_DEALS" };

    public static final String[] categories = { "dealofday", "top_deals", "food", "shopping", "electronics", "hotels_spas", "malls", "automotive", "travel", "entertainment", "jewelry", "sports_fitness" };

    public static final List<SubCategory> subCategories = new ArrayList();

    public static void setUpSubCategories(Activity activity) {

        if(subCategories.size() == 0) {

        /* FOOD & DINING */
            subCategories.add(new SubCategory(1, R.id.cb_food_arabic, activity.getString(R.string.Arabic), "food_arabic", CT_FOOD, false, false));
            subCategories.add(new SubCategory(2, R.id.cb_food_turkish, activity.getString(R.string.Turkish), "food_turkish", CT_FOOD, false, false));
            subCategories.add(new SubCategory(3, R.id.cb_food_indian, activity.getString(R.string.Indian), "food_indian", CT_FOOD, false, false));
            subCategories.add(new SubCategory(4, R.id.cb_food_chinese, activity.getString(R.string.Chinese), "food_chinese", CT_FOOD, false, false));
            subCategories.add(new SubCategory(5, R.id.cb_food_cafes, activity.getString(R.string.Cafes), "food_cafes", CT_FOOD, false, false));
            subCategories.add(new SubCategory(6, R.id.cb_fast_food, activity.getString(R.string.Fast_Food), "food_fast_food", CT_FOOD, false, false));
            subCategories.add(new SubCategory(7, R.id.cb_food_italian, activity.getString(R.string.Italian), "food_italian", CT_FOOD, false, false));
            subCategories.add(new SubCategory(8, R.id.cb_food_afghani, activity.getString(R.string.Afghani), "food_afghani", CT_FOOD, false, false));
            subCategories.add(new SubCategory(9, R.id.cb_food_lebanese, activity.getString(R.string.Lebanese), "food_lebanese", CT_FOOD, false, false));
            subCategories.add(new SubCategory(10, R.id.cb_food_sweets, activity.getString(R.string.Sweets_Confectionaries), "food_sweets", CT_FOOD, false, false));

        /* SHOPPING */
            subCategories.add(new SubCategory(11, R.id.cb_apparel, activity.getString(R.string.Apparel), "shopping_apparel", CT_SHOPPING, false, false));
            subCategories.add(new SubCategory(12, R.id.cb_home_goods, activity.getString(R.string.Home_Goods), "shopping_home_goods", CT_SHOPPING, false, false));
            subCategories.add(new SubCategory(13, R.id.cb_sports, activity.getString(R.string.Sports_Fitness), "shopping_sports_fitness", CT_SHOPPING, false, false));
            subCategories.add(new SubCategory(14, R.id.cb_shop_electronics, activity.getString(R.string.Electronics), "shopping_electronics", CT_SHOPPING, false, false));
            subCategories.add(new SubCategory(15, R.id.cb_fashion, activity.getString(R.string.Fashion), "shopping_fashion", CT_SHOPPING, false, false));

        /* ELECTRONICS */
            subCategories.add(new SubCategory(16, R.id.cb_tv, activity.getString(R.string.TV_Home_Entertainment), "electronics_tv_home", CT_ELECTRONICS, false, false));
            subCategories.add(new SubCategory(17, R.id.cb_computers, activity.getString(R.string.Computers_Tablets), "electronics_computer_tablets", CT_ELECTRONICS, false, false));
            subCategories.add(new SubCategory(18, R.id.cb_camera_photo, activity.getString(R.string.Cameras_Photography), "electronics_cameras_photography", CT_ELECTRONICS, false, false));
            subCategories.add(new SubCategory(19, R.id.cb_mobiles, activity.getString(R.string.Mobile_Phones_Accessories), "electronics_mobile", CT_ELECTRONICS, false, false));

        /* HOTELS & SPAS */
            subCategories.add(new SubCategory(20, R.id.cb_salons, activity.getString(R.string.Salons), "hotels_salons", CT_HOTELS, false, false));
            subCategories.add(new SubCategory(21, R.id.cb_logding, activity.getString(R.string.Lodging), "hotels_lodging", CT_HOTELS, false, false));
            subCategories.add(new SubCategory(22, R.id.cb_spas, activity.getString(R.string.Spas), "hotels_spas", CT_HOTELS, false, false));

        /* MARKETS & MALLS */
            subCategories.add(new SubCategory(23, R.id.cb_hypermarkets, activity.getString(R.string.Hypermarkets), "markets_hypermarkets", CT_MALLS, false, false));
            subCategories.add(new SubCategory(24, R.id.cb_malls, activity.getString(R.string.Malls), "markets_malls", CT_MALLS, false, false));

        /* AUTOMOTIVE */
            subCategories.add(new SubCategory(25, R.id.cb_showrooms, activity.getString(R.string.Show_Rooms), "automotives_showroom", CT_AUTOMOTIVE, false, false));
            subCategories.add(new SubCategory(26, R.id.cb_auto_accessories, activity.getString(R.string.Accessories), "automotives_accessories", CT_AUTOMOTIVE, false, false));
            subCategories.add(new SubCategory(27, R.id.cb_auto_services, activity.getString(R.string.Services), "automotives_services", CT_AUTOMOTIVE, false, false));

        /* ENTERTAINMENT */
            subCategories.add(new SubCategory(28, R.id.cb_events, activity.getString(R.string.Events), "entertainment_events", CT_ENTERTAINMENT, false, false));
            subCategories.add(new SubCategory(29, R.id.cb_kids_activities, activity.getString(R.string.Kids_Activities), "entertainment_kids_activities", CT_ENTERTAINMENT, false, false));
            subCategories.add(new SubCategory(30, R.id.cb_cinemas, activity.getString(R.string.Cinemas), "entertainment_cinemas", CT_ENTERTAINMENT, false, false));

        /* JEWELLERY & EXCHANGE */
            subCategories.add(new SubCategory(31, R.id.cb_gold_rate, activity.getString(R.string.Gold_Rate), "jewelry_gold", CT_JEWELLERY, false, false));
            subCategories.add(new SubCategory(32, R.id.cb_currency, activity.getString(R.string.Currency), "jewelry_currency", CT_JEWELLERY, false, false));

        /* SPORTS & FITNESS */
            subCategories.add(new SubCategory(33, R.id.cb_sports_clothing, activity.getString(R.string.Clothing), "sports_clothing", CT_SPORTS, false, false));
            subCategories.add(new SubCategory(34, R.id.cb_sports_equipment, activity.getString(R.string.Equipment), "sports_equipment", CT_SPORTS, false, false));
        }
    }

    public static synchronized void showSubCategories(Activity activity, int category) {
        setUpSubCategories(activity);
        for(SubCategory sc : subCategories) {
            if(sc.parent == category) {
                sc.isVisible = true;
                Logger.logI("SUB CATEGORY: " + sc.isSelected, sc.title);
                CheckBox checkBox = (CheckBox) activity.findViewById(sc.checkBoxId);
                checkBox.setText(sc.title);
                checkBox.setChecked(sc.isSelected);
                checkBox.setSelected(sc.isSelected);
                checkBox.setVisibility(View.VISIBLE);
            } else {
                activity.findViewById(sc.checkBoxId).setVisibility(View.GONE);
            }
        }
        CheckBox discountCheckBox = (CheckBox) activity.findViewById(R.id.cb_highest_discount);
        discountCheckBox.setText(activity.getString(R.string.sort_discount));
    }

    public static SubCategory getSubCategoryByCheckboxId(int checkBoxId) {
        for(SubCategory s : subCategories) {
            if(s.checkBoxId == checkBoxId) {
                return s;
            }
        }
        return null;
    }

    public static int getCategoryIcon(String subCategoryName) {
        int icon = R.drawable.ic_categories;

        if(isNotNullOrEmpty(subCategoryName)) {
            for(SubCategory subCategory : subCategories) {
                if(subCategory.title.equalsIgnoreCase(subCategoryName)) {
                    icon = subCategory.icon;
                }
            }
        }
        return icon;
    }

    public static String getCategoryFilter(String categoryName) {
        String filter = "";

        if(isNotNullOrEmpty(categoryName)) {
            for(SubCategory subCategory : subCategories) {
                if(subCategory.title.equalsIgnoreCase(categoryName)) {
                    filter = subCategory.code;
                    break;
                }
            }
        }
        return filter;
    }

    public static int getCategoryCheckboxId(String categoryName) {
        int checkboxId = -1;

        if(isNotNullOrEmpty(categoryName)) {
            for(SubCategory subCategory : subCategories) {
                if(subCategory.title.equalsIgnoreCase(categoryName)) {
                    checkboxId = subCategory.checkBoxId;
                    break;
                }
            }
        }
        return checkboxId;
    }

    public static void updateSubCategorySelection(int checkBoxId, boolean selected) {
        Logger.logI("updateSubCategorySelection", checkBoxId + "-" + selected);
        for(SubCategory s : subCategories) {
            if(s.checkBoxId == checkBoxId) {
                Logger.logI("updateSubCategorySelection", s.title + "-" + s.checkBoxId);
                s.isSelected = selected;
                break;
            }
        }
    }

    public static int getParentCategory(String subCategoryName) {
        int parent = 0;
        if(isNotNullOrEmpty(subCategoryName)) {
            for(SubCategory subCategory : subCategories) {
                if(subCategory.title.equalsIgnoreCase(subCategoryName)) {
                    parent = subCategory.parent;
                    break;
                }
            }
        }
        return parent;
    }

    public static List<SubCategory> getSubCategories(int parent) {
        List<SubCategory> subCategories = new ArrayList<>();
        if(parent > 0) {
            for(SubCategory subCategory : subCategories) {
                if(subCategory.parent == parent) {
                    subCategories.add(subCategory);
                }
            }
        }
        return subCategories;
    }

    public static void resetSubCategories(int parent) {
        for(SubCategory subCategory : subCategories) {
            if(subCategory.parent == parent) {
                subCategory.isSelected = false;
            }
        }
    }

    public static String getSelectedSubCategories(int category) {
        Logger.print("getSelectedSubCategories ---" + category);
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
