package com.ooredoo.bizstore.utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

public class AlphaForeGroundColorSpan extends ForegroundColorSpan {
    private float mAlpha;

    public AlphaForeGroundColorSpan(int color) {
        super(color);
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setColor(getAlphaColor());
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    private int getAlphaColor() {
        int foregroundColor = getForegroundColor();
        return Color.argb((int) (mAlpha * 255), Color.red(foregroundColor), Color.green(foregroundColor), Color.blue(foregroundColor));
    }
}