package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Babar on 22-Jun-15.
 */
public class Mall implements Serializable
{
  /*  public int id;

    @SerializedName("title")
    public String name;

    @SerializedName("images")
    public Image image;*/


    public int id, views, businessId;

    public String title, description, location, contact, address;

    public float rating;

    public boolean isFavorite;

    @SerializedName("images")
    public Image image;
}
