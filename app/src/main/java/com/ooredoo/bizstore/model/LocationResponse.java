package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class LocationResponse
{
    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public GenericDeal locationData;
}
