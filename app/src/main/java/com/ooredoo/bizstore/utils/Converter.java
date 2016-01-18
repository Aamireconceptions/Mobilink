package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Category;

/**
 * @author  Babar
 * @since 12-Jun-15.
 */
public class Converter
{
    public static float convertDpToPixels(float dp)
    {
        Resources resources = Resources.getSystem();

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());

       // Logger.print("DP: "+dp+ ", PX: "+px);

        return px;
    }

    public static Category convertCategoryText(Context context, String target)
    {
        if(target.equals("shopping & speciality stores"))
        {
            return new Category(context.getString(R.string.shopping_speciality), R.drawable.ic_shopping);
        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        if(target.equals(""))
        {

        }
        else
        {

        }

        return null;
    }
}
