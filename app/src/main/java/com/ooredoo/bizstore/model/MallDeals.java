package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 09-Feb-16.
 */
public class MallDeals
{
    public String category;

    @SerializedName("results")
    public List<GenericDeal> deals;
}
