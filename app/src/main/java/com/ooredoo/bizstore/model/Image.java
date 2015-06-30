package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Babar on 30-Jun-15.
 */
public class Image
{
    @SerializedName("logo")
    public String logoUrl;

    @SerializedName("banner")
    public String bannerUrl;
}
