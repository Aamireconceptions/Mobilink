package com.ooredoo.bizstore.model;

import com.ooredoo.bizstore.R;

import static com.ooredoo.bizstore.utils.CategoryUtils.CT_AUTOMOTIVE;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_ELECTRONICS;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_ENTERTAINMENT;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_FOOD;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_HOTELS;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_JEWELLERY;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_MALLS;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_SHOPPING;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_SPORTS;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_TOP;
import static com.ooredoo.bizstore.utils.CategoryUtils.CT_TRAVEL;

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
            case CT_ELECTRONICS:
                icon = R.drawable.ic_electronics;
                break;
            case CT_HOTELS:
                icon = R.drawable.ic_hotels;
                break;
            case CT_MALLS:
                icon = R.drawable.ic_malls;
                break;
            case CT_AUTOMOTIVE:
                icon = R.drawable.ic_automotive;
                break;
            case CT_TRAVEL:
                icon = R.drawable.ic_travel;
                break;
            case CT_ENTERTAINMENT:
                icon = R.drawable.ic_entertainment;
                break;
            case CT_JEWELLERY:
                icon = R.drawable.ic_jewellery;
                break;
            case CT_SPORTS:
                icon = R.drawable.ic_sports;
                break;
        }
        return icon;
    }
}
