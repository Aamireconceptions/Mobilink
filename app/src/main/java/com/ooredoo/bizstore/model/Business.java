package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Pehlaj Rai
 * @since 6/29/2015.
 */
public class Business  implements Serializable{

    public int id, views;

    public String title, description, location, contact, address;

    public float rating;

    public boolean isFavorite;

    @SerializedName("images")
    public Image image;

    public Business(Mall mall)
    {
        this.id = mall.id;
        this.title = mall.title;
        this.views = mall.views;
        this.rating = mall.rating;
        this.contact = mall.contact;
        this.address = mall.address;
        this.location = mall.location;
        this.description = mall.description;

        this.image = mall.image;
    }

    public Business(RecentItem recentItem)
    {
        this.id = recentItem.id;
        this.title = recentItem.title;
        this.views = recentItem.views;
        this.rating = recentItem.rating;
        this.contact = recentItem.contact;
        this.address = recentItem.address;
        //this.location = recentItem.location;
        this.description = recentItem.description;

        Image image = new Image();
        image.bannerUrl = recentItem.banner;
        image.detailBannerUrl = recentItem.detailBanner;

        this.image = image;

        //this.image = recentItem.image;
    }

    public Business(Brand brand)
    {
        this.id = brand.id;
        this.title = brand.title;
        this.views = brand.views;
        this.rating = brand.rating;
        this.contact = brand.contact;
        this.address = brand.address;
        this.location = brand.location;
        this.description = brand.description;

        this.image = brand.image;
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
         this.image = result.image;
       /* image.bannerUrl = result.banner;
        image.detailBannerUrl = result.detailBanner;*/


    }

    public Business(Favorite fav)
    {
        this.id = fav.id;
        this.title = fav.title;
        this.views = fav.views;
        this.rating = fav.rating;
        this.contact = fav.contact;
        this.address = fav.address;
       // this.location = fav.location;
        this.description = fav.description;

        Image image = new Image();
        image.bannerUrl = fav.banner;
        image.detailBannerUrl = fav.detailBanner;

        this.image = image;

        //this.image = fav.image;
    }
}
