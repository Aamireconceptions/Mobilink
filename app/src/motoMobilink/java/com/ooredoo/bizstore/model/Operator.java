package com.ooredoo.bizstore.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Babar on 08-Feb-17.
 */
public class Operator
{
    @SerializedName("result")
    public int resultCode = -1;

    public String desc = null;

    public String name;

    public String password;
}
