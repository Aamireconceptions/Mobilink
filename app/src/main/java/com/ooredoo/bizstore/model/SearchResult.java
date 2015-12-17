package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 06-Jul-2015.
 */

public class SearchResult {

    public int id, discount, views, businessId;

    public String type, title, category, description, startDate, endDate, contact, address, location,
            businessLogo, businessName, timing;

    public float rating;

    public boolean isFav;

    public int color = 0, is_exclusive;

    public double latitude, longitude;

    String banner, detailBanner;

    @SerializedName("images")
    public Image image;

    public List<Location> locations;
}