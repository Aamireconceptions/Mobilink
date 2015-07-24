package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.R;

/**
 * Created by Babar on 24-Jul-15.
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

        ImageView imageView = (ImageView) v.findViewById(R.id.image_view);

        prepareSlide(textView1, textView2, imageView);
    }

    private void prepareSlide(TextView textView1, TextView textView2, ImageView imageView)
    {
        Activity activity = getActivity();

        int slideNum = getArguments().getInt(SLIDE_NUM);

        String text1 = null;
        String text2 = null;
        int resId = 0;

        switch (slideNum)
        {
            case 0:
                text1 = activity.getString(R.string.slide_1_text_1);
                text2 = activity.getString(R.string.slide_1_text_2);
                resId = R.drawable.slide_1;
                break;

            case 1:
                text1 = activity.getString(R.string.slide_2_text_1);
                text2 = activity.getString(R.string.slide_2_text_2);
                resId = R.drawable.slide_2;
                break;

            case 2:
                text1 = activity.getString(R.string.slide_3_text_1);
                text2 = activity.getString(R.string.slide_3_text_2);
                resId = R.drawable.slide_3;

                break;

            case 3:
                text1 = activity.getString(R.string.slide_4_text_1);
                text2 = activity.getString(R.string.slide_4_text_2);
                resId = R.drawable.slide_4;

                break;
        }

        textView1.setText(text1);
        textView2.setText(text2);
        imageView.setImageResource(resId);
    }
}