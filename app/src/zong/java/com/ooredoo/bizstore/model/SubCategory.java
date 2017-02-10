package com.ooredoo.bizstore.model;

import com.ooredoo.bizstore.R;

import static com.ooredoo.bizstore.utils.CategoryUtils.CT_EDUCATION;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_ENTERTAINMENT;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_FOOD;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_HEALTH;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_LADIES;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_SHOPPING;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_TOP;

/**
 * @author Pehlaj Rai
 * @since 04-Jul-15.
 */

public class SubCategory {
    public int id;

    public int checkBoxId;

    public String title;

    public String code; //code being used on server to filter data e.g. food_arabic

    public int parent; //Parent Category

    public boolean isSelected, isVisible;

    public int icon; //Parent category icon

    public SubCategory() {
    }

    public SubCategory(int id, int checkBoxId, String title, String code, int parent, boolean isVisible, boolean isSelected) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.parent = parent;
        this.isVisible = isVisible;
        this.isSelected = isSelected;
        this.checkBoxId = checkBoxId;
        this.icon = getParentCategoryIcon(parent);
    }

    public int getParentCategoryIcon(int parent) {
        int icon = R.drawable.ic_categories;

        switch(parent) {
            case CT_TOP:
                icon = R.drawable.ic_top_deals;
                break;
            case CT_FOOD:
                icon = R.drawable.ic_food_dining;
                break;
            case CT_SHOPPING:
                icon = R.drawable.ic_shopping;
                break;
            case CT_LADIES:
                icon = R.drawable.ic_electronics;
                break;
            case CT_HEALTH:
                icon = R.drawable.ic_hotels;
                break;
            case CT_EDUCATION:
                icon = R.drawable.ic_malls;
                break;

            case CT_ENTERTAINMENT:
                icon = R.drawable.ic_entertainment;
                break;

        }
        return icon;
    }
}
