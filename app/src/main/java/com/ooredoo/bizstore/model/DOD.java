package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Babar on 13-Jan-16.
 */
public class DOD
{
    @SerializedName("dod_category")
    public String category;

    @SerializedName("results")
    public List<GenericDeal> deals;
}
