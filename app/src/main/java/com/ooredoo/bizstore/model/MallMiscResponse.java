package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Babar on 07-Jul-15.
 */
public class MallMiscResponse
{
    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public MallMiscResult result;
}
