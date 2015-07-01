package com.ooredoo.bizstore.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Pehlaj Rai
 * @since 2/3/2015.
 */
@Table(name = "notifications")
public class Notification extends Model {

    @Column(name = "notificationId")
    public long notificationId; //Uppercase to avoid conflict

    @Column(name = "icon", notNull = false)
    public int icon;

    @Column(name = "title")
    public String title;

    @Column(name = "isEnabled")
    public boolean enabled;

    public Notification(long id, int icon, String title, boolean enabled) {
        this.icon = icon;
        this.title = title;
        this.enabled = enabled;
        this.notificationId = id;
    }
}
