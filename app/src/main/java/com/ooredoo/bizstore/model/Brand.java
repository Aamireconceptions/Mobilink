package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 22-Jun-15.
 */
public class Brand
{
    public int id;

    @SerializedName("images")
    public Image image;
}
