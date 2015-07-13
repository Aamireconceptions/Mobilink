package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Pehlaj Rai.
 * @since 03/20/2014.
 */
public class Subscription {

    @SerializedName("result")
    public int resultCode = -1;

    public String desc = null;

    public String password;
}