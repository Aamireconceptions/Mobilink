package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 07-Jul-15.
 */
public class MallResponse
{
    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public List<Mall> malls;
}
