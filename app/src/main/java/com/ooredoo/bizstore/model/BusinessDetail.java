package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Pehlaj Rai.
 * @since 03/20/2014.
 */
public class BusinessDetail {

    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public Business src;
}