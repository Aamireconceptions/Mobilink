package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Babar on 07-Aug-15.
 */
public class Voucher
{
    @SerializedName("result")
    public int resultCode;

    @SerializedName("voucher")
    public String code;
}
