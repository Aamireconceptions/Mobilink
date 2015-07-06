package com.ooredoo.bizstore.model;

/**
 * @author Pehlaj Rai
 * @since 6/29/2015.
 */
public class Business {

    public int id, views;

    public String title, description, contact, address;

    public float rating;

    public Business() {
    }

    public Business(SearchResult result) {
        this.id = result.id;
        this.title = result.title;
        this.views = result.views;
        this.rating = result.rating;
        this.contact = result.contact;
        this.address = result.address;
        this.description = result.description;
    }
}
