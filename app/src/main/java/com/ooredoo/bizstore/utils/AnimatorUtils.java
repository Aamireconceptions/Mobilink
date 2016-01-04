package com.ooredoo.bizstore.utils;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;

/**
 * Created by Babar on 01-Jan-16.
 */
public class AnimatorUtils
{
    public static void fadeIn(View v)
    {
        v.setAlpha(0);
        v.animate().alpha(1).setDuration(200);

       /* ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 1f);
        fadeIn.setDuration(200);
        fadeIn.start();*/
    }

    public static void slideUp(Context context, View v) {

        ObjectAnimator slideUp = (ObjectAnimator) AnimatorInflater.loadAnimator(context, R.animator.slide_up_animator);
        slideUp.setTarget(v);
        slideUp.start();


        //v.setY(v.getBottom() + 100);

       /* v.setY(v.getY() + 100);
        v.animate().translationY(v.getY() - 100).setDuration(300);*/
    }

    /*public static void fadeIn(Context context, Bitmap bitmap)
    {
        BitmapDrawable bmpDrawable = new BitmapDrawable(context.getResources(), bitmap);
        bmpDrawable.setAlpha(0);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(bmpDrawable, "alpha", 1);
        fadeOut.setDuration(3000);
        fadeOut.start();
       // ObjectAnimator fadeIn = ObjectAnimator.ofFloat(bitmap, "alpha", 1);

        *//*AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(3000);
        //animatorSet.playTogether(fadeOut, fadeIn);
        animatorSet.playSequentially(fadeOut, fadeIn);
        animatorSet.start();*//*
    }*/
}
