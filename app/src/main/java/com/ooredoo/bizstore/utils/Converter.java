package com.ooredoo.bizstore.utils;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

       // Logger.print("DP: "+dp+ ", PX: "+px);

        return px;
    }

    public static Category convertCategoryText(Context context, String target)
    {
        if(target.equals("Food & Dining"))
        {
            return new Category(context.getString(R.string.food_dining), R.drawable.ic_food_dining);
        }
        else
        if(target.equals("Shopping & Specialty Stores") || target.equals("Shopping") || target.equals("Shopping and Fashion"))
        {
            return new Category(context.getString(R.string.shopping_speciality), R.drawable.ic_shopping);
        }
        else
        if(target.equals("Electronics"))
        {
            return new Category(context.getString(R.string.electronics), R.drawable.ic_electronics);
        }
        else
        if(target.equals("Hotels & Spas"))
        {
            return new Category(context.getString(R.string.hotels_spa), R.drawable.ic_hotels);
        }
        else
        if(target.equals("Markets & Malls"))
        {
            return new Category(context.getString(R.string.markets_malls), R.drawable.ic_malls);
        }
        else
        if(target.equals("Automotives"))
        {
            return new Category(context.getString(R.string.automotive), R.drawable.ic_automotive);
        }
        else
        if(target.equals("Travel and Tours"))
        {
            return new Category(context.getString(R.string.travel_tours), R.drawable.ic_travel);
        }
        else
        if(target.equals("Entertainment"))
        {
            return new Category(context.getString(R.string.entertainment), R.drawable.ic_entertainment);
        }
        else
        if(target.equals("Jewelry & Exchange"))
        {
            return new Category(context.getString(R.string.jewelry_exchange), R.drawable.ic_jewellery);
        }
        else
        if(target.equals("Sports & Fitness"))
        {
            return new Category(context.getString(R.string.sports_fitness), R.drawable.ic_sports);
        }
        if(target.equals("lifestyle"))
        {
            return new Category(context.getString(R.string.lifestyle), R.drawable.ic_ladies);
        }

        return new Category(context.getString(R.string.sports_fitness), R.drawable.ic_sports);
    }
}
