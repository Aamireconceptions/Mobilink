package com.ooredoo.bizstore.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;
import com.ooredoo.bizstore.utils.Logger;

import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */

@Table(name = "obs_recent_items")
public class RecentItem extends Model {

    @SerializedName("id")
    @Column(name = "itemId", notNull = true)
    public int id;

    @Column(name = "type", notNull = false)
    public int type;

    @Column(name = "daysLeft", notNull = false)
    public int daysLeft;

    @Column(name = "businessId", notNull = false)
    public long businessId;

    @Column(name = "desc")
    public String description;

    @Column(name = "city", notNull = false)
    public String city;

    @Column(name = "title")
    public String title;

    @Column(name = "contact", notNull = false)
    public String contact;

    @Column(name = "address", notNull = false)
    public String address;

    @Column(name = "category", notNull = false)
    public String category;

    @Column(name = "discount")
    public int discount;

    @Column(name = "views", notNull = false)
    public int views;

    @Column(name = "rating", notNull = false)
    public float rating;

    @Column(name = "isFavorite")
    public boolean isFavorite;

    @Column(name = "isBusiness")
    public boolean isBusiness;

    @Column(name = "banner")
    public String banner;

    @Column(name = "detail_banner")
    public String detailBanner;

    @Column(name = "latitude")
    public double latitude;

    @Column(name = "longitude")
    public double longitude;

    @Column(name = "business_name")
    public String businessName;

    @Column(name = "businessLogo")
    public String businessLogo;

    @Column(name = "brand_address")
    public String brandAddress;

    @Column(name = "location")
    public String location;

    @Column(name = "end_date")
    public String endDate;

    public int color = 0;

    /*@Column(name = "locations")
    public List<Location> locations;*/

    public RecentItem() {
    }

    public RecentItem(int id, int type, String title, int discount, String desc, String city) {
        this.id = id;
        this.type = type;
        this.description = desc;
        this.city = city;
        this.title = title;
        this.discount = discount;
    }

    public RecentItem(GenericDeal deal) {
        this.id = deal.id;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.contact = deal.contact;
        this.address = deal.address;
        this.isFavorite = deal.isFav;
        this.discount = deal.discount;
        this.category = deal.category;
        this.description = deal.description;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFav));
    }

    public RecentItem(RecentDeal deal) {
        this.id = deal.id;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.address = deal.address;
        this.contact = deal.contact;
        this.discount = deal.discount;
        this.category = deal.category;
        this.isFavorite = deal.isFavorite;
        this.description = deal.description;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
    }

    public RecentItem(Business business) {
        this.id = business.id;
        this.title = business.title;
        this.views = business.views;
        this.rating = business.rating;
        this.address = business.address;
        this.contact = business.contact;
        this.discount = 0;
        this.category = "";
        this.isFavorite = business.isFavorite;
        this.description = business.description;
        Logger.logI("DEAL: " + business.id, String.valueOf(business.isFavorite));
    }

    public static boolean isFavorite(long dealId) {
        List<RecentItem> favorites = new Select().all().from(RecentItem.class).where("itemId = " + dealId + " AND isFavorite = 1").execute();

        if(favorites != null && favorites.size() > 0) {
            return favorites.get(0).isFavorite;
        }
        return false;
    }

    public static void updateFavorite(RecentItem favorite) {
        if(favorite != null && favorite.id > 0) {
            RecentItem favDeal = new RecentItem();
            List<RecentItem> favorites = new Select().all().from(RecentItem.class).where("itemId=" + favorite.id).execute();
            if(favorites != null && favorites.size() > 0) {
                favDeal = favorites.get(0);
            }
            favDeal.id = favorite.id;
            favDeal.city = favorite.city;
            favDeal.description = favorite.description;
            favDeal.type = favorite.type;
            favDeal.views = favorite.views;
            favDeal.title = favorite.title;
            favDeal.rating = favorite.rating;
            favDeal.category = favorite.category;
            favDeal.discount = favorite.discount;
            favDeal.isFavorite = favorite.isFavorite;
            favDeal.description = favorite.description;
            favDeal.contact = favorite.contact;
            favDeal.address = favorite.address;
            Log.i("UPDATE_FAV_DEAL: " + favorite.id, "---" + favorite.isFavorite);
            favDeal.save();
        }
    }
}