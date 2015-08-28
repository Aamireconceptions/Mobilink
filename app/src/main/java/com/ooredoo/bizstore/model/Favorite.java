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

@Table(name = "obs_favorites")
public class Favorite extends Model {

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

    public Favorite() {
    }

    public Favorite(int id, int type, String title, int discount, String desc, String city) {
        this.id = id;
        this.type = type;
        this.description = desc;
        this.city = city;
        this.title = title;
        this.discount = discount;
    }

    public Favorite(GenericDeal deal) {
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

    public Favorite(RecentDeal deal) {
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

    public Favorite(Business business) {
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
        Logger.logI("FAV_BUSINESS: " + business.id, String.valueOf(business.isFavorite));
    }

    public static void clearFavorites() {
        List<Favorite> favorites = new Select().all().from(Favorite.class).execute();
        for(Favorite favorite : favorites) {
            favorite.delete();
        }
    }

    public Favorite(RecentItem recentItem) {
        this.id = recentItem.id;
        this.title = recentItem.title;
        this.views = recentItem.views;
        this.rating = recentItem.rating;
        this.address = recentItem.address;
        this.contact = recentItem.contact;
        this.discount = recentItem.discount;
        this.category = recentItem.category;
        this.isFavorite = recentItem.isFavorite;
        this.description = recentItem.description;
        Logger.logI("FAV: " + recentItem.id, String.valueOf(recentItem.isFavorite));
    }

    public static boolean isFavorite(long favoriteId) {
        List<Favorite> favorites = new Select().all().from(Favorite.class).where("itemId = " + favoriteId + " AND isFavorite = 1").execute();

        if(favorites != null && favorites.size() > 0) {
            return favorites.get(0).isFavorite;
        }
        return false;
    }

    public static void updateFavorite(Favorite favorite) {
        if(favorite != null && favorite.id > 0) {
            Favorite fav = new Favorite();
            List<Favorite> favorites = new Select().all().from(Favorite.class).where("itemId=" + favorite.id).execute();
            if(favorites != null && favorites.size() > 0) {
                fav = favorites.get(0);
            }
            fav.isBusiness = favorite.isBusiness;
            fav.id = favorite.id;
            fav.city = favorite.city;
            fav.description = favorite.description;
            fav.type = favorite.type;
            fav.views = favorite.views;
            fav.title = favorite.title;
            fav.rating = favorite.rating;
            fav.category = favorite.category;
            fav.discount = favorite.discount;
            fav.isFavorite = favorite.isFavorite;
            fav.description = favorite.description;
            fav.address = favorite.address;
            fav.contact = favorite.contact;
            Log.i("UPDATE_FAV_DEAL: " + favorite.id, "---" + favorite.isFavorite);
            fav.save();
        }
    }
}