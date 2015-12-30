package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Babar
 * @since 30-Jun-15.
 */
public class Image implements Serializable
{
    @SerializedName("logo")
    public String logoUrl;

    @SerializedName("banner")
    public String bannerUrl;

    @SerializedName("promotional")
    public String promotionalUrl;

    public String featured;

    @SerializedName("grid")
    public String gridBannerUrl;

    @SerializedName("detail")
    public String detailBannerUrl;

}
