package com.ooredoo.bizstore.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */

@Table(name = "obs_recent_deals")
public class RecentDeal extends Model {

    @Column(name = "dealId", notNull = true)
    public int id;

    @Column(name = "type", notNull = false)
    public int type;

    @Column(name = "views", notNull = false)
    public int views;

    @Column(name = "rating", notNull = false)
    public float rating;

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

    @Column(name = "isFavorite")
    public boolean isFavorite;

    public String endDate;
    public RecentDeal() {
    }

    public static boolean isFavorite(RecentDeal deal) {
        List<Favorite> deals = new Select().all().from(Favorite.class).where("dealId = " + deal.id + " AND isFavorite = 1").execute();

        if(deals != null && deals.size() > 0) {
            return deals.get(0).isFavorite;
        }
        return false;
    }
}
