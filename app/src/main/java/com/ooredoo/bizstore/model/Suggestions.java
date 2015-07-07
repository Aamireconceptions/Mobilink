package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Pehlaj Rai
 * @since 22-Jun-15.
 */
public class Suggestions {

    @SerializedName("result")
    public int resultCode = -1;

    @SerializedName("results")
    public String[] list = {};
}
