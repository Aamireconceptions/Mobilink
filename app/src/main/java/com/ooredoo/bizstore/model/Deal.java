package com.ooredoo.bizstore.model;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */

public class Deal {

    public long id;
    public int daysLeft;
    public long restaurantId;

    public String type;
    public String city;
    public String title;
    public String teaser;
    public String startDate;
    public String endDate;
    public String desc;

    public boolean isFavorite;
}
