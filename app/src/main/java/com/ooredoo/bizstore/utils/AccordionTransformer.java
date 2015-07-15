package com.ooredoo.bizstore.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

public class AccordionTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.1f;

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();

        int pageHeight = page.getHeight();

        if(position >= -1 && position <= 1) {
            // Modify the default slide transition to shrink the page as well

            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));

            float vertMargin = pageHeight * (1 - scaleFactor) / 2;

            float horzMargin = pageWidth * (1 - scaleFactor) / 2;

            if(position < 0) {
                page.setTranslationX(horzMargin - vertMargin / 50);
            } else {
                page.setTranslationX(-horzMargin + vertMargin / 50);
            }

            // Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
        }
    }
}