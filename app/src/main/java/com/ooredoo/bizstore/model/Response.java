package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class Response
{
    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public List<GenericDeal> deals;

    @SerializedName("top_banner")
    public String topBannerUrl;

    public String password;

    public String desc;

    @SerializedName("token")
    public String apiToken;
}
