package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;
import com.ooredoo.bizstore.utils.Logger;

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

    public GenericDeal(RecentDeal deal) {
        this.id = deal.id;
        this.detail = deal.desc;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.discount = deal.discount;
        this.category = deal.category;
        this.isFav = deal.isFavorite;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
    }

    public GenericDeal(Deal deal) {
        this.id = deal.id;
        this.detail = deal.desc;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.discount = deal.discount;
        this.category = deal.category;
        this.isFav = deal.isFavorite;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
    }
}
