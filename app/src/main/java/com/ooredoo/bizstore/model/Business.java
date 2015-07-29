package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Pehlaj Rai
 * @since 6/29/2015.
 */
public class Business {

    public int id, views;

    public String title, description, location, contact, address;

    public float rating;

    @SerializedName("images")
    public Image image;

    public Business() {
    }

    public Business(SearchResult result) {
        this.id = result.id;
        this.title = result.title;
        this.views = result.views;
        this.rating = result.rating;
        this.contact = result.contact;
        this.address = result.address;
        this.location = result.location;
        this.description = result.description;
    }
}
