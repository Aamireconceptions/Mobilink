package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.ooredoo.bizstore.BizStore;

/**
 * Created by Babar on 18-Jun-15.
 */
public class SnackBarUtils
{
    private Context context;

    private View parent;

    public SnackBarUtils(Context context, View parent)
    {
        this.context = context;

        this.parent = parent;
    }

    public  void showSimple(int resId, int duration)
    {
        Snackbar.make(parent, resId, duration).show();
    }

}
