package com.ooredoo.bizstore.asynctasks;

import com.google.gson.annotations.SerializedName;
import com.ooredoo.bizstore.model.GenericDeal;

import java.util.List;

/**
 * Created by Babar on 03-Nov-15.
 */

public class DealMisc
{
    @SerializedName("result")
    public int resultCode;

    @SerializedName("results")
    public GenericDeal genericDeal;


}
