package com.ooredoo.bizstore.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Babar on 21-Sep-15.
 */
@Table(name="obs_cities")
public class City extends Model
{
    @Column(name="cityId", notNull = true)
    public long id;

    @Column(name="res_id", notNull = true)
    public int resId;

    @Column(name = "name", notNull = true)
    public String name;

    @Column(name = "is_checked", notNull = true)
    public boolean isChecked;

    public City()
    {

    }

    public City(long id, boolean isChecked, int resId, String name)
    {
        this.id = id;

        this.isChecked = isChecked;

        this.resId = resId;

        this.name = name;
    }

    @Override
    public String toString() {
        return "cityId: " + id + ", Name: " + name + ", resId : " + resId + ", isChecked : " + isChecked;
    }
}