package com.ooredoo.bizstore.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.utils.Logger;

/**
 * @author Babar
 * @since 23-Jul-15.
 */
public class CustomExpandableListView extends ExpandableListView
{
    private View navigationHeader;

    public CustomExpandableListView(Context context, View navigationHeader)
    {
        super(context);

        this.navigationHeader = navigationHeader;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {


        int extraBuffer = 400;

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(navigationHeader.getWidth(), MeasureSpec.EXACTLY);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels + extraBuffer, MeasureSpec.AT_MOST);

        Logger.print("widthMesaureSpec: "+widthMeasureSpec+", heightMeasureSpec: "+heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}