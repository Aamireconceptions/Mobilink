package com.ooredoo.bizstore.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by Babar on 19-Jan-16.
 */
public class CustomHorizontalScroll extends HorizontalScrollView
{

    public CustomHorizontalScroll(Context context)
    {
        super(context);
    }

    public CustomHorizontalScroll(Context context, AttributeSet attr)
    {
        super(context, attr);
    }

    public CustomHorizontalScroll(Context context, AttributeSet attr, int style)
    {
        super(context, attr, style);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        View v = getChildAt(0);

        super.onMeasure(v.getMeasuredWidth(), v.getMeasuredHeight());
    }
}
