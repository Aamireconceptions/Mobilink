package com.ooredoo.bizstore.model;

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
    }
}
