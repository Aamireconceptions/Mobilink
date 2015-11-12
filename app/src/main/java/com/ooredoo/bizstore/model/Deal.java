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

    @Column(name = "banner")
    public String banner;

    @Column(name = "detail_banner")
    public String detailBanner;

    @Column(name = "lat")
    public double lat;

    @Column(name = "lng")
    public double lng;

    @Column(name = "business_name")
    public String businessName;

    @Column(name = "businessLogo")
    public String businessLogo;

    @Column(name = "brand_address")
    public String brandAddress;


    public Deal() {
    }

    public Deal(int id, int type, String title, int discount, String desc, String city) {
        this.id = id;
        this.type = type;
        this.description = desc;
        this.city = city;
        this.title = title;
        this.discount = discount;
    }

    public Deal(GenericDeal deal) {
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
        this.banner = deal.image != null ? deal.image.bannerUrl : null;
        this.detailBanner = deal.image != null ? deal.image.detailBannerUrl : null;

        this.lat = deal.latitude;
        this.lng = deal.longitude;
        this.brandAddress = deal.brandAddress;
        this.businessLogo = deal.businessLogo;
        this.businessName = deal.businessName;

        Logger.logI("DEAL: " + deal.id, String.valueOf(deal.isFav));
    }

    public Deal(RecentDeal deal) {
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
            favDeal.description = deal.description;
            favDeal.type = deal.type;
            favDeal.views = deal.views;
            favDeal.title = deal.title;
            favDeal.rating = deal.rating;
            favDeal.category = deal.category;
            favDeal.discount = deal.discount;
            favDeal.isFavorite = deal.isFavorite;
            favDeal.description = deal.description;
            Log.i("UPDATE_FAV_DEAL: " + deal.id, "---" + deal.isFavorite);
            favDeal.save();
        }
    }
}