package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;
import com.ooredoo.bizstore.utils.Logger;

import java.io.Serializable;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public class GenericDeal implements Serializable{

    public int id, views, discount;

    public String description, startDate, endDate, voucher, status, redeemedOn;

    public String title, category, contact, address;

    @SerializedName("images")
    public Image image;

    public boolean isFav;

    public float rating;

    public GenericDeal(RecentDeal deal) {
        this.id = deal.id;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.address = deal.address;
        this.contact = deal.contact;
        this.discount = deal.discount;
        this.category = deal.category;
        this.isFav = deal.isFavorite;
        this.description = deal.description;
        this.endDate = deal.endDate;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
    }

    public GenericDeal(SearchResult result) {
        this.id = result.id;
        this.title = result.title;
        this.views = result.views;
        this.rating = result.rating;
        this.address = result.address;
        this.contact = result.contact;
        this.discount = result.discount;
        this.category = result.category;

        //this.isFav = result.isFavorite;
        this.description = result.description;
        this.endDate = result.endDate;

       // Logger.logI("DEAL: " + result.id, String.valueOf(deal.isFavorite));
    }

    public GenericDeal(Deal deal) {
        this.id = deal.id;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.address = deal.address;
        this.contact = deal.contact;
        this.discount = deal.discount;
        this.category = deal.category;
        this.isFav = deal.isFavorite;
        this.description = deal.description;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
    }
}
