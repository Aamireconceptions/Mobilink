package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;

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

    private static String LOLLIPOP_DEFAULT_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Futura/FuturaLT-Book.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : BuildConfig.FLAVOR.equals("mobilink")
            ? "fonts/lato_regular.ttf" :  BuildConfig.FLAVOR.equals("zong")
            ? "fonts/lato_regular.ttf" : "fonts/OpenSans-Regular.ttf";

    private final static String ARABIC_DEFAULT_FONT = "fonts/Arabic/GE SS Unique Light.otf";

    public static void setDefaultFont(Context context, String staticTypefaceFieldName,
                                      String fontAssetName)
    {
        Typeface typeface;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            String font = BizStore.getLanguage().equals("en") ? LOLLIPOP_DEFAULT_FONT : ARABIC_DEFAULT_FONT;

            typeface = Typeface.createFromAsset(context.getAssets(), font);

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

    public static void setFont(Context context, TextView textView)
    {
        String path = BizStore.getLanguage().equals("en") ? BizStore.DEFAULT_FONT :
                BizStore.ARABIC_DEFAULT_FONT;

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);

        textView.setTypeface(typeface);
    }

    public static void setFont(Context context, String path, TextView textView)
    {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);

        textView.setTypeface(typeface);
    }

    public static void setFontWithStyle(Context context, TextView textView, int style)
    {
        String path = BizStore.getLanguage().equals("en") ? BizStore.DEFAULT_FONT :
                BizStore.ARABIC_DEFAULT_FONT;

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);

        textView.setTypeface(typeface, style);
    }


    public static void changeColor(TextView textView, String text, String part, int color)
    {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, part.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);
    }

    public static void strikeThrough(TextView textView, String text,  String part, int color)
    {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new StrikethroughSpan(), text.length() - part.length(), text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(color), text.length() - part.length(), text.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);
    }

    public static void changeColorAndMakeBold(TextView textView, String text, String part, int color)
    {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, part.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, part.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(spannable);
    }


    public static void changeColorAndMakeBold(TextView textView, String text, String part, int color,
                                              Context context, String path)
    {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, part.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, part.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        textView.setText(spannable);
    }
}