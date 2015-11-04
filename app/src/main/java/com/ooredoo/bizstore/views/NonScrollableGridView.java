package com.ooredoo.bizstore.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.ooredoo.bizstore.utils.Logger;

/**
 * Created by Babar on 04-Nov-15.
 */
public class NonScrollableGridView extends GridView
{
    public NonScrollableGridView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {


        // Do not use the highest two bits of Integer.MAX_VALUE because they are
        // reserved for the MeasureSpec mode

        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightSpec);

        getLayoutParams().height = getMeasuredHeight();

        Logger.print("GridView Height:"+getLayoutParams().height);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }
}
