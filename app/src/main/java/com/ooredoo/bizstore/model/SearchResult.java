package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Pehlaj Rai
 * @since 06-Jul-2015.
 */

public class SearchResult {

    public int id, discount, views, businessId;

    public String type, title, category, description, startDate, endDate, contact, address, location;

    public float rating;

    String banner, detailBanner;

    @SerializedName("images")
    public Image image;
}