package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ooredoo.bizstore.R;

/**
 * Created by pehlaj.rai on 7/14/2015.
 */
public class AnimUtils {

    public static int DURATION = 400;

    public static void slideView(Activity activity, View view, boolean slideInBottom) {

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_top);

        if(slideInBottom) {
            animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom);
        }

        animation.setDuration(DURATION);
        view.startAnimation(animation);
    }

}
