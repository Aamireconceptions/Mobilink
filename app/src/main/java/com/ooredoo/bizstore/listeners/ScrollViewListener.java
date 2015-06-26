package com.ooredoo.bizstore.listeners;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;

import com.ooredoo.bizstore.utils.AlphaForeGroundColorSpan;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.ScrollViewHelper;

/**
 * @author Pehlaj Rai
 * @since 6/26/2015.
 */
public class ScrollViewListener implements ScrollViewHelper.OnScrollViewListener {
    ColorDrawable cd = new ColorDrawable(ColorUtils.RED);
    ActionBar actionBar;
    private AlphaForeGroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    public ScrollViewListener(ActionBar actionBar) {
        this.actionBar = actionBar;
        mSpannableString = new SpannableString(actionBar.getTitle());
        mAlphaForegroundColorSpan = new AlphaForeGroundColorSpan(0xFFFFFF);
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(cd);
        cd.setAlpha(0);
    }

    @Override
    public void onScrollChanged(ScrollViewHelper v, int l, int t, int oldl, int oldt) {
        setTitleAlpha(255 - getAlphaforActionBar(v.getScrollY()));
        cd.setAlpha(getAlphaforActionBar(v.getScrollY()));
    }

    private int getAlphaforActionBar(int scrollY) {
        int minDist = 0, maxDist = 550;
        if(scrollY > maxDist) {
            return 255;
        } else {
            if(scrollY < minDist) {
                return 0;
            } else {
                return (int) ((255.0 / maxDist) * scrollY);
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
