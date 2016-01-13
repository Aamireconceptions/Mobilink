package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 13-Jan-16.
 */
public class DODResponse
{
    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public List<DOD> dods;
}
