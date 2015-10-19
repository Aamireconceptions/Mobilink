package com.ooredoo.bizstore.ui.activities;

import android.view.View;
import android.widget.Button;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.StringUtils;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.APP_LANGUAGE;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getBooleanVal;

public class MainActivity extends BaseActivity {

    Button btnArabicLang, btnEnglishLang;
    public MainActivity() {
        super();
        layoutResId = R.layout.activity_main;
    }

    @Override
    public void init() {
        btnArabicLang = (Button) findViewById(R.id.btn_lang_arabic);

        FontUtils.setFont(this, BizStore.ARABIC_DEFAULT_FONT, btnArabicLang);

        btnEnglishLang = (Button) findViewById(R.id.btn_lang_english);
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
            SharedPrefUtils.updateVal(this, APP_LANGUAGE, language);
            BizStore.setLanguage(language);
            btnArabicLang.setSelected(isArabic);
            btnEnglishLang.setSelected(!isArabic);

            BizStore bizStore = (BizStore) getApplication();
            bizStore.overrideDefaultFonts();

            NavigationMenuOnClickListener.updateConfiguration(this, language);

            startActivity(SignUpActivity.class);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        boolean check = getBooleanVal(this, SharedPrefUtils.LOGIN_STATUS);
        if(check) {
            startActivity(HomeActivity.class);
        }
    }
}
