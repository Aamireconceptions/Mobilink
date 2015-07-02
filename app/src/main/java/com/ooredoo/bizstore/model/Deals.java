package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Pehlaj Rai.
 * @since 03/20/2014.
 */
public class Deals {

    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public List<Deal> list;
}