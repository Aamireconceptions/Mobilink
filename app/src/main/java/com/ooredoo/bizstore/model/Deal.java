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

@Table(name = "obs_deals")
public class Deal extends Model {

    @SerializedName("id")
    @Column(name = "dealId", notNull = true)
    public int id;

    @Column(name = "type", notNull = false)
    public int type;

    @Column(name = "daysLeft", notNull = false)
    public int daysLeft;

    @Column(name = "businessId", notNull = false)
    public long businessId;

    @Column(name = "desc")
    public String desc;

    @Column(name = "city", notNull = false)
    public String city;

    @Column(name = "title")
    public String title;

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

    public Deal() {
    }

    public Deal(int id, int type, String title, int discount, String desc, String city) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.city = city;
        this.title = title;
        this.discount = discount;
    }

    public Deal(GenericDeal deal) {
        this.id = deal.id;
        this.desc = deal.detail;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.isFavorite = deal.isFav;
        this.discount = deal.discount;
        this.category = deal.category;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFav));
    }

    public Deal(RecentDeal deal) {
        this.id = deal.id;
        this.desc = deal.desc;
        this.title = deal.title;
        this.views = deal.views;
        this.rating = deal.rating;
        this.discount = deal.discount;
        this.category = deal.category;
        this.isFavorite = deal.isFavorite;
        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFavorite));
    }

    public static boolean isFavorite(long dealId) {
        List<Deal> deals = new Select().all().from(Deal.class).where("dealId = " + dealId + " AND isFavorite = 1").execute();

        if(deals != null && deals.size() > 0) {
            return deals.get(0).isFavorite;
        }
        return false;
    }

    public static void updateDealAsFavorite(Deal deal) {
        if(deal != null && deal.id > 0) {
            Deal favDeal = new Deal();
            List<Deal> deals = new Select().all().from(Deal.class).where("dealId=" + deal.id).execute();
            if(deals != null && deals.size() > 0) {
                favDeal = deals.get(0);
            }
            favDeal.id = deal.id;
            favDeal.city = deal.city;
            favDeal.desc = deal.desc;
            favDeal.type = deal.type;
            favDeal.views = deal.views;
            favDeal.title = deal.title;
            favDeal.rating = deal.rating;
            favDeal.category = deal.category;
            favDeal.discount = deal.discount;
            favDeal.isFavorite = deal.isFavorite;
            Log.i("UPDATE_FAV_DEAL: " + deal.id, "---" + deal.isFavorite);
            favDeal.save();
        }
    }
}