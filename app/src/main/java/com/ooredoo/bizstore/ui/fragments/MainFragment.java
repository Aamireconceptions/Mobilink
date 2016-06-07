package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.StringUtils;

import static com.ooredoo.bizstore.utils.FragmentUtils.addFragmentWithBackStack;
import static com.ooredoo.bizstore.utils.FragmentUtils.replaceFragmentWithBackStack;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.APP_LANGUAGE;

/**
 * Created by Babar on 07-Jun-16.
 */
public class MainFragment extends Fragment implements View.OnClickListener
{
    Button btnArabicLang, btnEnglishLang;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        init(v);
        return v;
    }

    Activity context;
    private void init(View v)
    {
        context = getActivity();

        btnArabicLang = (Button) v.findViewById(R.id.btn_lang_arabic);

        FontUtils.setFont(context, btnArabicLang);

        btnEnglishLang = (Button) v.findViewById(R.id.btn_lang_english);
        btnArabicLang.setOnClickListener(this);
        btnEnglishLang.setOnClickListener(this);
        String lang = BizStore.getLanguage();

        boolean isArabicLang = StringUtils.isNotNullOrEmpty(lang) && lang.equals("ar");

        btnArabicLang.setSelected(isArabicLang);
        btnEnglishLang.setSelected(!isArabicLang);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_lang_arabic || viewId == R.id.btn_lang_english) {
            boolean isArabic = viewId == R.id.btn_lang_arabic;
            String language = viewId == R.id.btn_lang_arabic ? "ar" : "en";
            SharedPrefUtils.updateVal(context, APP_LANGUAGE, language);
            BizStore.setLanguage(language);
            btnArabicLang.setSelected(isArabic);
            btnEnglishLang.setSelected(!isArabic);

            BizStore bizStore = (BizStore) context.getApplication();
            bizStore.overrideDefaultFonts();

            NavigationMenuOnClickListener.updateConfiguration(context, language);

            if(BuildConfig.FLAVOR.equals("ooredoo")) {
                replaceFragmentWithBackStack((AppCompatActivity) getActivity(), R.id.fragment_container,
                        new SignInFragment(), "signin_frag");
            }
            else
            {
                replaceFragmentWithBackStack((AppCompatActivity) getActivity(), R.id.fragment_container,
                        new DemoFragment(), "demo_fragment");
            }
        }
    }
}