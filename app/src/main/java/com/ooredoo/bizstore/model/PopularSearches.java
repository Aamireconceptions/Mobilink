package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 22-Jun-15.
 */
public class PopularSearches {

    @SerializedName("result")
    public int resultCode = -1;

    @SerializedName("results")
    public List<KeywordSearch> list = new ArrayList<>();
}
