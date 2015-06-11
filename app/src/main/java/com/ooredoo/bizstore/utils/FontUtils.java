package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Babar on 10-Jun-15.
 */
public class FontUtils
{
    private static String sanSerif = "sans-serif";

    private static String lollipopTypefaceFieldName = "sSystemFontMap";

    public static void setDefaultFont(Context context, String staticTypefaceFieldName,
                                      String fontAssetName)
    {
        final Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontAssetName);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Map<String, Typeface> map = new HashMap<>();
            map.put(sanSerif, typeface);

            replaceFont(lollipopTypefaceFieldName, map);
        }
        else
        {
            replaceFont(staticTypefaceFieldName, typeface);
        }
    }

    private static void replaceFont(String staticTypefaceFieldName, final Object newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
