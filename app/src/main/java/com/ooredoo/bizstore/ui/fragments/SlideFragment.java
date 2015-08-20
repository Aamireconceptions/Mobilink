package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.ColorUtils;

/**
 * @author Babar
 * @since 24-Jul-15.
 */
public class SlideFragment extends Fragment
{
    private final static String SLIDE_NUM = "slide_num";

    public static SlideFragment newInstance(int slideNum)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(SLIDE_NUM, slideNum);

        SlideFragment slideFragment = new SlideFragment();
        slideFragment.setArguments(bundle);
        
        return slideFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_slide, container, false);

        init(v);

        return v;
    }

    private void init(View v)
    {
        TextView textView1 = (TextView) v.findViewById(R.id.text_1);

        TextView textView2 = (TextView) v.findViewById(R.id.text_2);

        TextView textView3 = (TextView) v.findViewById(R.id.text_3);

        ImageView imageView = (ImageView) v.findViewById(R.id.image_view);

        prepareSlide(textView1, textView2, textView3, imageView);
    }

    private void prepareSlide(TextView textView1, TextView textView2, TextView textView3, ImageView imageView)
    {
        Activity activity = getActivity();

        int slideNum = getArguments().getInt(SLIDE_NUM);

        String text1 = null;
        String text2 = null;
        String text3 = null;
        int resId = 0;

        switch (slideNum)
        {
            case 0:
                textView3.setTextColor(ColorUtils.RED);
                textView2.setTextColor(ColorUtils.BLACK);
                textView3.setTypeface(null, Typeface.BOLD);
                textView2.setTypeface(null, Typeface.NORMAL);
                text1 = activity.getString(R.string.slide_1_text_1);
                text2 = activity.getString(R.string.slide_1_text_2);
                text3 = activity.getString(R.string.slide_1_text_3);
                resId = R.drawable.slide_1;
                break;

            case 1:
                textView2.setTextColor(ColorUtils.RED);
                textView3.setTextColor(ColorUtils.BLACK);
                textView2.setTypeface(null, Typeface.BOLD);
                textView3.setTypeface(null, Typeface.NORMAL);
                text1 = activity.getString(R.string.slide_2_text_1);
                text2 = activity.getString(R.string.slide_2_text_2);
                text3 = activity.getString(R.string.slide_2_text_3);
                resId = R.drawable.slide_2;
                break;

            case 2:
                /*textView2.setTextColor(ColorUtils.RED);
                textView3.setTextColor(ColorUtils.BLACK);
                textView2.setTypeface(null, Typeface.BOLD);
                textView3.setTypeface(null, Typeface.NORMAL);
                text1 = activity.getString(R.string.slide_4_text_1);
                text2 = activity.getString(R.string.slide_4_text_2);
                text3 = activity.getString(R.string.slide_4_text_3);
                resId = R.drawable.slide_3;

                break;

            case 3:*/
                textView3.setTextColor(ColorUtils.RED);
                textView2.setTextColor(ColorUtils.BLACK);
                textView3.setTypeface(null, Typeface.BOLD);
                textView2.setTypeface(null, Typeface.NORMAL);
                text1 = activity.getString(R.string.slide_3_text_1);
                text2 = activity.getString(R.string.slide_3_text_2);
                text3 = activity.getString(R.string.slide_3_text_3);

                resId = R.drawable.slide_4;

                break;
        }

        textView1.setText(text1);
        textView2.setText(text2);
        textView3.setText(text3);
        imageView.setImageResource(resId);
    }
}