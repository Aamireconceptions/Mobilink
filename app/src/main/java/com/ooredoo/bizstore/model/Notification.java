package com.ooredoo.bizstore.model;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */

public class Notification {

    public long id;
    public int icon;
    public String title;
    public boolean enabled;

    public Notification(long id, int icon, String title, boolean enabled) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.enabled = enabled;
    }
}
