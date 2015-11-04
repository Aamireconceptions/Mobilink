package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Babar on 22-Jun-15.
 */
public class Brand implements Serializable
{
    /*public int id;

    @SerializedName("images")
    public Image image;*/

    public int id;

    public int views;

    public String title, description, location, contact, address, businessLogo;

    double latitude, longitude;

    public float rating;

    public boolean isFavorite;

    @SerializedName("images")
    public Image image;
}
