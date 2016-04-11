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

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.ColorUtils;
import com.ooredoo.bizstore.utils.FontUtils;

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

        String font = BizStore.getLanguage().equals("en") ? BizStore.DEFAULT_FONT : BizStore.ARABIC_DEFAULT_FONT;
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), font);

        switch (slideNum)
        {
            case 0:

                textView1.setTextColor(ColorUtils.BLACK);
                textView2.setTextColor(ColorUtils.BLACK);
                textView3.setTextColor(ColorUtils.RED);
                textView3.setTypeface(typeface, Typeface.BOLD);
               // textView2.setTypeface(typeface, Typeface.NORMAL);
                text1 = activity.getString(R.string.slide_1_text_1);
                text2 = activity.getString(R.string.slide_1_text_2);
                text3 = activity.getString(R.string.slide_1_text_3);
                resId = R.drawable.slide_1;
                break;

            case 1:
                textView1.setTextColor(ColorUtils.BLACK);
                textView2.setTextColor(ColorUtils.RED);
                textView2.setTypeface(typeface, Typeface.BOLD);
                textView3.setTextColor(ColorUtils.BLACK);

                text1 = activity.getString(R.string.slide_2_text_1);
                text2 = activity.getString(R.string.slide_2_text_2);
                text3 = activity.getString(R.string.slide_2_text_3);
                resId = R.drawable.slide_2;
                break;

            case 2:

                if(BuildConfig.FLAVOR.equals("dealionare")) {
                    textView1.setVisibility(View.GONE);
                }
                textView1.setTextColor(ColorUtils.BLACK);
                textView2.setTextColor(ColorUtils.BLACK);
                textView3.setTextColor(ColorUtils.RED);
                textView3.setTypeface(typeface, Typeface.BOLD);

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