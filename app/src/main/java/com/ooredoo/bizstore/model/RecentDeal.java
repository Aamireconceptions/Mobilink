package com.ooredoo.bizstore.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */

@Table(name = "obs_recent_deals")
public class RecentDeal extends Model {

    @Column(name = "dealId", notNull = true)
    public long id;

    @Column(name = "type", notNull = false)
    public int type;

    @Column(name = "daysLeft", notNull = false)
    public int daysLeft;

    @Column(name = "restaurantId", notNull = false)
    public long restaurantId;

    @Column(name = "desc")
    public String desc;

    @Column(name = "city", notNull = false)
    public String city;

    @Column(name = "title")
    public String title;

    @Column(name = "discount")
    public int discount;

    @Column(name = "isFavorite")
    public boolean isFavorite;

    public RecentDeal() {
    }

    public RecentDeal(long id, int type, String title, int discount, String desc, String city) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.city = city;
        this.title = title;
        this.discount = discount;
    }

}
