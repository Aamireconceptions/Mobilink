package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Babar on 07-Aug-15.
 */
public class Voucher
{
    @SerializedName("result")
    public int resultCode;

    public String desc;

    @SerializedName("voucher")
    public String code;

    public int vouchers_claimed;

    public int max_allowed;
}
