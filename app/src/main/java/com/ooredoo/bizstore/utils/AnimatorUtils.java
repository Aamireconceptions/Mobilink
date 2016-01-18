package com.ooredoo.bizstore.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

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

     //v.animate().alpha(0).setDuration(500);
        v.animate().alphaBy(1).setDuration(150);


       /* ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 1f);
        fadeIn.setDuration(200);
        fadeIn.start();*/
    }

    public static void fadeOut(View v)
    {
        v.animate().alpha(0).setDuration(500);
    }

    public static void slideUp(Context context, View v) {

        /*ObjectAnimator slideUp = (ObjectAnimator) AnimatorInflater.loadAnimator(context, R.animator.slide_up_animator);
        slideUp.setTarget(v);
        slideUp.start();*/

        Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        v.startAnimation(slideUp);


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

    public static void expandAndFadeIn(View v)
    {

        //v.setAlpha(0);
        //v.setScaleX(0.5f);
        //v.setScaleY(0.5f);

        //v.animate().scaleX(1f).scaleY(1f).setDuration(500);

       /* ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, View.ALPHA, 0f, 1f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 0f, 1f);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 0f, 1f);*/

        PropertyValuesHolder fadeIn = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 0.6f, 1f, 1.3f, 1.5f, 1.3f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 0.6f, 1f, 1.3f, 1.5f, 1.3f, 1f);

        ValueAnimator va = ObjectAnimator.ofPropertyValuesHolder(v, scaleX, scaleY, fadeIn);
        va.setDuration(1500);
        va.start();

        /*AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(fadeIn, scaleX, scaleY);
        animatorSet.start();*/
    }

    public static void startDetailAnimation(View imageView, TableLayout tableLayout,
                                     LinearLayout linearLayout)
    {
        // Logger.print("right: "+imageView.getRight() + ", width: "+ imageView.getWidth()+ ", sum: "+(imageView.getRight() + imageView.getWidth()));

        ObjectAnimator imageTranslateX = ObjectAnimator.ofFloat(imageView, "translationX",
                imageView.getRight() + imageView.getWidth(), 0);

        ObjectAnimator tableTranslateX = ObjectAnimator.ofFloat(tableLayout, "translationX",
                tableLayout.getLeft() - tableLayout.getWidth(), 0);

        ObjectAnimator discountTranslateY = ObjectAnimator.ofFloat(linearLayout, "translationY",
                linearLayout.getTop() + linearLayout.getHeight() / 2, 0);

        //ObjectAnimator translate
        /*translateX.setDuration(1000);
        translateX.start();*/

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(700);
        animatorSet.playTogether(imageTranslateX, discountTranslateY);
        animatorSet.start();
    }

    public static void showFab(final FloatingActionButton fab)
    {
        fab.post(new Runnable() {
            @Override
            public void run() {
                float y = fab.getY();
                float top = fab.getTop();

                float yValue = Converter.convertDpToPixels(60);

                Logger.print("fab Y," + y + ", top:" + top + " y needed:" + (top - yValue));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    yValue += Converter.convertDpToPixels(16);
                }

                PropertyValuesHolder pvhTranslateY = PropertyValuesHolder.ofFloat(View.Y,
                        (top - yValue));

                PropertyValuesHolder pvhFadeIn = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);

                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fab, pvhTranslateY, pvhFadeIn);
                animator.setDuration(300);
                animator.start();
            }
        });
    }

    static ObjectAnimator animator;

    public static void hideFab(final FloatingActionButton fab)
    {
        if(animator != null && animator.isRunning())
        {
            return;
        }

        fab.post(new Runnable() {
            @Override
            public void run() {

                float fabTop = fab.getTop();

                float yValue = Converter.convertDpToPixels(60);

                PropertyValuesHolder pvhTranslateY = PropertyValuesHolder.ofFloat(View.Y, fabTop + yValue);

                PropertyValuesHolder pvhFadeOut = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

                animator = ObjectAnimator.ofPropertyValuesHolder(fab, pvhTranslateY, pvhFadeOut);
                animator.setDuration(300);
                animator.start();

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        });
    }


}