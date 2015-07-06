package com.ooredoo.bizstore.model;

/**
 * @author Pehlaj Rai
 * @since 6/29/2015.
 */
public class Business {

    public int id, views;

    public String title, desc, contact, address;

    public float rating;

    public Business() {
    }

    public Business(SearchResult result) {
        this.id = result.id;
        this.desc = result.desc;
        this.title = result.title;
        this.views = result.views;
        this.rating = result.rating;
        this.address = result.address;
        this.contact = result.contact;
    }
}
