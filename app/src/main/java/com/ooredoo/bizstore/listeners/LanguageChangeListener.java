package com.ooredoo.bizstore.listeners;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.ColorUtils;

/**
 * @author Pehlaj Rai
 * @since 6/19/2015.
 */
public class LanguageChangeListener implements View.OnClickListener {

    public ExpandableListView expandableListView;
    View navigationHeaderView;
    HomeActivity mActivity;

    Button btnLangEnglish, btnLangArabic;

    public LanguageChangeListener(HomeActivity mActivity, View navigationHeaderView) {
        this.mActivity = mActivity;
        this.navigationHeaderView = navigationHeaderView;
        btnLangArabic = (Button) navigationHeaderView.findViewById(R.id.btn_lang_arabic);
        btnLangEnglish = (Button) navigationHeaderView.findViewById(R.id.btn_lang_english);
        btnLangArabic.setOnClickListener(this);
        btnLangEnglish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_lang_english || id == R.id.btn_lang_arabic) {
            boolean isBtnArabicLang = id == R.id.btn_lang_arabic;
            HomeActivity.rtl = isBtnArabicLang;
            //changeDirection(isBtnArabicLang);
            btnLangArabic.setTextColor(isBtnArabicLang ? ColorUtils.WHITE : ColorUtils.BLACK);
            btnLangEnglish.setTextColor(isBtnArabicLang ? ColorUtils.BLACK : ColorUtils.WHITE);
            btnLangArabic.setBackgroundResource(isBtnArabicLang ? R.drawable.btn_red2 : R.drawable.btn_lt_grey2);
            btnLangEnglish.setBackgroundResource(isBtnArabicLang ? R.drawable.btn_lt_grey1 : R.drawable.btn_red1);
            changeDirection(isBtnArabicLang);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeDirection(boolean isArabicLanguage) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mActivity.getWindow().getDecorView().setLayoutDirection(isArabicLanguage ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);*/
        expandableListView.setLayoutDirection(isArabicLanguage ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        expandableListView.setTextDirection(isArabicLanguage ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
        expandableListView.setTextAlignment(isArabicLanguage ? View.TEXT_ALIGNMENT_VIEW_END : View.TEXT_ALIGNMENT_VIEW_START);
        expandableListView.collapseGroup(0);
        expandableListView.collapseGroup(1);
    }
}
