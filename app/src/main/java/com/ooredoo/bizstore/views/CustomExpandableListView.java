package com.ooredoo.bizstore.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.utils.NavigationMenuUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Babar on 23-Jul-15.
 */
public class CustomExpandableListView extends ExpandableListView
{
    int groupPos, childPos, groupId;




    public CustomExpandableListView(Context context)
    {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, MeasureSpec.EXACTLY);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, MeasureSpec.AT_MOST);

        /*widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(600, MeasureSpec.AT_MOST);*/

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}