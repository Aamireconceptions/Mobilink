package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("images")
    public Image image;

    public boolean isFav;

    public float rating;
}
