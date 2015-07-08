package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Babar on 22-Jun-15.
 */
public class Mall
{
    public int id;

    @SerializedName("title")
    public String name;

    @SerializedName("images")
    public Image image;
}
