package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;
import com.ooredoo.bizstore.adapters.Gallery;

import java.io.Serializable;
import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 6/29/2015.
 */
public class Business  implements Serializable{

    public int id;

    public List<Location> locations;

    public int views;

    public String title, description, location, contact, address, businessLogo, timing, businessName;

    public float rating;

    public boolean isFavorite;

    @SerializedName("images")
    public Image image;

    public double latitude, longitude;

    public int color;

    public String type;

    @SerializedName("menu")
    public List<Menu> menus;

    public List<Gallery> galleries;

    @SerializedName("deals")
    public List<GenericDeal> moreDeals;

    @SerializedName("relatedBusiness")
    public List<Brand> similarBrands;

    public int businessId;

    public double distance;

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

        this.color = mall.color;

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

        this.location = recentItem.location;
        this.businessId = (int) recentItem.businessId;
        this.businessLogo = recentItem.businessLogo;
        this.latitude = recentItem.latitude;
        this.longitude = recentItem.longitude;


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
        this.businessLogo = brand.businessLogo;
        this.latitude = brand.latitude;
        this.longitude = brand.longitude;
        this.businessId = brand.businessId;
        this.locations = brand.locations;

        this.image = brand.image;

        this.color = brand.color;
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
        this.timing = result.timing;

        this.businessId = result.businessId;
        this.businessLogo = result.businessLogo;
        this.latitude = result.latitude;
        this.longitude = result.longitude;
        this.color = result.color;
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
       this.location = fav.location;
        this.description = fav.description;

        Image image = new Image();
        image.bannerUrl = fav.banner;
        image.detailBannerUrl = fav.detailBanner;

        this.image = image;

        this.timing = fav.timing;


        this.businessId = (int) fav.businessId;
        this.businessLogo = fav.businessLogo;
        this.businessName = fav.title;
        this.latitude = fav.lat;
        this.longitude = fav.lng;
       // this.color = fav.color;

        //this.image = fav.image;
    }

    public Business(GenericDeal deal)
    {
        this.id = deal.id;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.contact = deal.contact;
        this.address = deal.address;
        this.location = deal.location;
        this.description = deal.description;
        this.businessLogo = deal.businessLogo;
        this.latitude = deal.latitude;
        this.longitude = deal.longitude;

        this.image = deal.image;

        this.color = deal.color;
    }
}
