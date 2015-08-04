package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pehlaj Rai.
 * @since 03/20/2014.
 */
public class KeywordSearch {

    public String title;

    @SerializedName("details")
    public List<SearchResult> results = new ArrayList<>();
}