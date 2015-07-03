package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public class GenericDeal {

    public int id, views, discount;

    @SerializedName("desc")
    public String detail;

    public String title, category, contact, address;

    @SerializedName("images")
    public Image image;

    public boolean isFav;

    public float rating;
}
