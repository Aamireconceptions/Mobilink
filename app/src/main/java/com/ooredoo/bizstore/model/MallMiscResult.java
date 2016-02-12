package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 12-Feb-16.
 */
public class MallMiscResult {

    @SerializedName("malls_deals")
    public List<MallDeals> deals;

    @SerializedName("malls_brands")
    public List<MallBrands> brands;
}
