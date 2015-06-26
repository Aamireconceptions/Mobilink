package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public class GenericDeal
{
    public int id, views;

    @SerializedName("desc")
    public String detail;

    public String title, discount, category;

    public boolean isFav;

    public float rating;
}
