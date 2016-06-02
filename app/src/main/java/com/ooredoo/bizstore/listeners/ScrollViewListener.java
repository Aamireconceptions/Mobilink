package com.ooredoo.bizstore.listeners;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.AbsListView;

import com.ooredoo.bizstore.utils.AlphaForeGroundColorSpan;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.ScrollViewHelper;

/**
 * @author Pehlaj Rai
 * @since 6/26/2015.
 */
public class ScrollViewListener  {
    ColorDrawable cd = new ColorDrawable(ColorUtils.RED);
    ActionBar actionBar;
    private AlphaForeGroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    private final int scrollOffset = 255;
    public ScrollViewListener(ActionBar actionBar) {
        this.actionBar = actionBar;
        mSpannableString = new SpannableString(actionBar.getTitle());
        mAlphaForegroundColorSpan = new AlphaForeGroundColorSpan(0xFFFFFF);
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(cd);
        cd.setAlpha(0);
    }

    public void setTitle(String title)
    {
        mSpannableString = new SpannableString(title);
    }



   /* public void onScrollChanged(ScrollViewHelper v, int l, int t, int oldl, int oldt) {
        setTitleAlpha(scrollOffset - getAlphaforActionBar(v.getScrollY()));
        cd.setAlpha(getAlphaforActionBar(v.getScrollY()));
    }*/

    public void onScrollChanged(View v) {

       // View v = av.getChildAt(0);

        if(v != null) {


                setTitleAlpha(scrollOffset - getAlphaforActionBar(-v.getTop()));
                cd.setAlpha(getAlphaforActionBar(-v.getTop()));

                Logger.print("onScroll Alpha: " + v.getTop());

        }

    }

    private int getAlphaforActionBar(int scrollY) {
        int minDist = 0, maxDist = 255;
        if(scrollY > maxDist) {
            return scrollOffset;
        } else {
            if(scrollY < minDist) {
                return 0;
            } else {
                return (scrollOffset / maxDist) * scrollY;
            }
        }
    }

    private void setTitleAlpha(float alpha) {
        if(alpha < 1) { alpha = 1; }
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(mSpannableString);
    }
}
