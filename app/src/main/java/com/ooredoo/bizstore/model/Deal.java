package com.ooredoo.bizstore.model;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */

public class Deal {

    public long id;
    public int type;
    public int daysLeft;
    public long restaurantId;

    public String desc;
    public String city;
    public String title;
    public String teaser;
    public String endDate;
    public String startDate;
    public String discount;

    public boolean isFavorite;

    public Deal() {
    }

    public Deal(long id, int type, String title, String discount, String desc, String city) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.city = city;
        this.title = title;
        this.discount = discount;
    }
}
