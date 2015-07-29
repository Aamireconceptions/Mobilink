package com.ooredoo.bizstore.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
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
        /*int extraBuffer = 400;

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        float screenWidth = displayMetrics.widthPixels / Resources.getSystem().getDisplayMetrics().density;
        float navWidth = screenWidth - 56;

        navWidth = Math.min(navWidth, 320);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, navWidth, displayMetrics);*/

        int extraBuffer = 400;

        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(navigationHeader.getWidth(), MeasureSpec.EXACTLY);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels + extraBuffer, MeasureSpec.AT_MOST);

        /*widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(600, MeasureSpec.AT_MOST);*/

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}