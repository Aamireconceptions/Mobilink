package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 25-Jun-15.
 */
public class Response
{
    @SerializedName("results")
    public List<GenericDeal> deals;

    public List<Brand> brands;

    public List<Mall> malls;
}