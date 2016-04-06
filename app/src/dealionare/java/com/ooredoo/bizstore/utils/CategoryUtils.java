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
    public static final int CT_LADIES = 4;
    public static final int CT_HEALTH = 5;
    public static final int CT_EDUCATION = 6;
    public static final int CT_ENTERTAINMENT = 7;
    public static final int CT_AUTOMOTIVE = 8;
    public static final int CT_ELECTRONICS = 9;
    public static final int CT_HOTELS = 10;
    public static final int CT_JEWELLERY = 11;
    public static final int CT_MALLS = 13;
    public static final int CT_NEARBY = 12;
    public static final int CT_SPORTS = 14;
    public static final int CT_TRAVEL = 15;
    public static final int CT_LIFESTYLE = 16;

    public static final String[] CACHE_KEYS = { "TOP_BRANDS", "TOP_MALLS", "PROMO_DEALS", "FEATURED_DEALS" };

    public static final String[] categories = { "dealofday", "top_deals", "food", "shopping", "ladies",
            "health", "education", "entertainment" };

    public static final List<SubCategory> subCategories = new ArrayList();

    public static void setUpSubCategories(Activity activity) {

        if(subCategories.size() == 0) {

        /* FOOD & DINING */
            subCategories.add(new SubCategory(1, R.id.cb_food_fast_food, activity.getString(R.string.fast_food), "food_fast_food", CT_FOOD, false, false));
            subCategories.add(new SubCategory(2, R.id.cb_food_restaurants, activity.getString(R.string.restaurants), "food_restaurants", CT_FOOD, false, false));
            subCategories.add(new SubCategory(3, R.id.cb_food_cafe, activity.getString(R.string.cafe), "food_cafe", CT_FOOD, false, false));

        /* SHOPPING */
            subCategories.add(new SubCategory(11, R.id.cb_clothing, activity.getString(R.string.clothing), "shopping_clothing", CT_SHOPPING, false, false));
            subCategories.add(new SubCategory(12, R.id.cb_home_appliances, activity.getString(R.string.home_appliances), "shopping_home_appliance", CT_SHOPPING, false, false));
            subCategories.add(new SubCategory(13, R.id.cb_others, activity.getString(R.string.others), "shopping_others", CT_SHOPPING, false, false));

        /* LIFESTYLE */
            subCategories.add(new SubCategory(16, R.id.cb_spas_salons, activity.getString(R.string.spas_salons), "lifestyle_spas_salons", CT_LIFESTYLE, false, false));
            subCategories.add(new SubCategory(17, R.id.cb_fitness, activity.getString(R.string.fitness), "lifestyle_fitness", CT_LIFESTYLE, false, false));

        /* ENTERTAINMENT */
            subCategories.add(new SubCategory(28, R.id.cb_cinemas, activity.getString(R.string.cinemas), "entertainment_cinemas", CT_ENTERTAINMENT, false, false));
            subCategories.add(new SubCategory(29, R.id.cb_entertainment_others, activity.getString(R.string.talk_shows), "entertainment_others", CT_ENTERTAINMENT, false, false));

        /* NEAR BY */
            subCategories.add(new SubCategory(36, R.id.cb_food_dining, activity.getString(R.string.food_dining), "food", CT_NEARBY, false, false));
            subCategories.add(new SubCategory(37, R.id.cb_shopping, activity.getString(R.string.shopping), "shopping", CT_NEARBY, false, false));
            subCategories.add(new SubCategory(38, R.id.cb_lifestyle, activity.getString(R.string.lifestyle), "lifestyle", CT_NEARBY, false, false));
            subCategories.add(new SubCategory(41, R.id.cb_entertainment, activity.getString(R.string.entertainment), "entertainment", CT_NEARBY, false, false));

        /* TOP */
            subCategories.add(new SubCategory(46, R.id.cb_food_dining1, activity.getString(R.string.food_dining), "food", CT_TOP, false, false));
            subCategories.add(new SubCategory(47, R.id.cb_shopping1, activity.getString(R.string.shopping), "shopping", CT_TOP, false, false));
            subCategories.add(new SubCategory(48, R.id.cb_lifestyle1, activity.getString(R.string.lifestyle), "lifestyle", CT_TOP, false, false));
            subCategories.add(new SubCategory(53, R.id.cb_entertainment1, activity.getString(R.string.entertainment), "entertainment", CT_TOP, false, false));
        }
    }

    public static synchronized void showSubCategories(Activity activity, final int category) {
        setUpSubCategories(activity);
        Logger.logI("showSubCategories", String.valueOf(category));
        for(SubCategory sc : subCategories) {
            if(sc.parent == category) {
                sc.isVisible = true;
                Logger.logI("SUB CATEGORY: " + sc.parent +":" + sc.isSelected, sc.title);
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
        Logger.logI("getParentCategory", subCategoryName);
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

    public static String getSelectedSubCategoriesForTag(int parent) {
        String categories = "";

        int i = 0;

        for(SubCategory subCategory : subCategories)
        {
            if(subCategory.parent == parent)
            {
                if(subCategory.isSelected)
                {
                    /*if(i != 0)
                    {
                        categories += ", " + subCategory.title;
                    }
                    else
                    {
                        categories = subCategory.title;
                    }*/
                    categories += subCategory.title + ", ";
                }
            }

            i++;
        }

        return categories;
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

    public static void resetCheckboxes()
    {
        for(SubCategory s : subCategories)
        {
            s.isSelected = false;
        }
    }

    public static void uncheckCheckBoxes(Activity activity)
    {
        for(SubCategory sc : subCategories) {
                CheckBox checkBox = (CheckBox) activity.findViewById(sc.checkBoxId);

                checkBox.setChecked(false);
                checkBox.setSelected(false);
            }
    }
}