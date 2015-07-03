package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Babar
 * @since 10-Jun-15.
 */
public class FontUtils
{
    private static String sanSerif = "sans-serif";

    private static String lollipopTypefaceFieldName = "sSystemFontMap";

    private final static String LOLLIPOP_DEFAULT_FONT = "fonts/Futura/FuturaLT-Book.ttf";

    public static void setDefaultFont(Context context, String staticTypefaceFieldName,
                                      String fontAssetName)
    {
        Typeface typeface;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            typeface = Typeface.createFromAsset(context.getAssets(), LOLLIPOP_DEFAULT_FONT);

            Map<String, Typeface> map = new HashMap<>();
            map.put(sanSerif, typeface);

            replaceFont(lollipopTypefaceFieldName, map);
        }
        else
        {
            typeface = Typeface.createFromAsset(context.getAssets(), fontAssetName);

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

    public static void setFont(Context context, String path, TextView textView)
    {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);

        textView.setTypeface(typeface);
    }
}