package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pehlaj Rai.
 * @since 13-Nov-2015.
 */
public class SearchSuggestions {

    @SerializedName("result")
    public int resultCode = -1;

    @SerializedName("results")
    public List<String> list = new ArrayList<>();
}