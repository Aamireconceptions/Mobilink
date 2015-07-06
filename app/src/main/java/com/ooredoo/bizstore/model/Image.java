package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Babar
 * @since 30-Jun-15.
 */
public class Image
{
    @SerializedName("logo")
    public String logoUrl;

    @SerializedName("banner")
    public String bannerUrl;

    @SerializedName("grid")
    public String gridBannerUrl;

    @SerializedName("detail")
    public String detailBannerUrl;
}
