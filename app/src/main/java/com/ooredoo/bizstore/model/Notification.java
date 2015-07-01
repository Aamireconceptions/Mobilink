package com.ooredoo.bizstore.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */
@Table(name = "obs_notifications")
public class Notification extends Model {

    @Column(name = "notificationId", notNull = true)
    public long id;

    @Column(name = "icon", notNull = false)
    public int icon;

    @Column(name = "title")
    public String title;

    @Column(name = "isEnabled")
    public boolean enabled;

    public Notification() {

    }

    public Notification(long id, boolean enabled, int icon, String title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Title: " + title + ", Icon : " + icon + ", Enabled : " + enabled;
    }
}
