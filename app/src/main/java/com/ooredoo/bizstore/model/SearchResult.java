package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 06-Jul-2015.
 */

public class SearchResult {

    public int id, discount, views, businessId, voucher_count;

    public String type, title, category, description, startDate, endDate, contact, address, location,
            businessLogo, businessName, timing, how_works;

    public float rating, mDistance;

    public boolean isFav;

    public int color = 0, is_exclusive;

    public int actualPrice, discountedPrice;

    public double latitude, longitude;

    String banner, detailBanner;

    @SerializedName("images")
    public Image image;

    public List<Location> locations;

    public boolean isBannerDisplayed;
}