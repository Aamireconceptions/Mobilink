package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;
import com.ooredoo.bizstore.utils.Logger;

import java.io.Serializable;
import java.util.List;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public class GenericDeal implements Serializable{

    public int id, views, discount, is_exclusive;

    public String description, startDate, endDate, voucher, status, redeemedOn, businessLogo, businessName;

    public String title, category, contact, address, location, brandName, brandAddress;

    @SerializedName("images")
    public Image image;

    public boolean isFav;

    public float rating;

    public double latitude, longitude;

    @SerializedName("relatedDeals")
    public List<GenericDeal> similarDeals;

    @SerializedName("nearDeals")
    public List<GenericDeal> nearbyDeals;

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

    public GenericDeal(RecentItem recentItem) {
        this.id = recentItem.id;
        this.title = recentItem.title;
        this.views = recentItem.views;
        this.rating = recentItem.rating;
        this.address = recentItem.address;
        this.contact = recentItem.contact;
        this.discount = recentItem.discount;
        this.category = recentItem.category;
        this.isFav = recentItem.isFavorite;
        this.description = recentItem.description;
        Image image = new Image();
        image.bannerUrl = recentItem.banner;
        image.detailBannerUrl = recentItem.detailBanner;
        this.image = image;

        //this.endDate = recentItem.endDate;
       // Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
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
        this.image = result.image;

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

    public GenericDeal(Brand brand) {
        this.id = brand.id;
        this.title = brand.title;
        this.image = brand.image;
        this.views = brand.views;
        this.rating = brand.rating;
        this.address = brand.address;
        this.contact = brand.contact;
       // this.discount = brand.discount;
        //this.category = brand.category;
        this.isFav = brand.isFavorite;
        this.description = brand.description;
        //Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
    }

    public GenericDeal(Favorite fav) {
        this.id = fav.id;
        this.title = fav.title;
        this.views = fav.views;
        this.rating = fav.rating;
        this.address = fav.address;
        this.contact = fav.contact;
        this.discount = fav.discount;
        this.category = fav.category;
        Image image = new Image();
        image.bannerUrl = fav.banner;
        image.detailBannerUrl = fav.detailBanner;
        this.image = image;
        //this.isFav = result.isFavorite;
        this.description = fav.description;
        //this.endDate = fav.endDate;

        // Logger.logI("DEAL: " + result.id, String.valueOf(deal.isFavorite));
    }
}
