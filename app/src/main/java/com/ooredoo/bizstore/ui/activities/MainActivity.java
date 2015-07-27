package com.ooredoo.bizstore.ui.activities;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.FragmentUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import static com.ooredoo.bizstore.BizStore.setLanguage;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getBooleanVal;

public class MainActivity extends BaseActivity {

    public MainActivity() {
        super();
        layoutResId = R.layout.activity_main;
    }

    @Override
    public void init() {
        findViewById(R.id.btn_lang_arabic).setOnClickListener(this);
        findViewById(R.id.btn_lang_english).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_lang_arabic || viewId == R.id.btn_lang_english) {
            setLanguage(viewId == R.id.btn_lang_arabic ? "ar" : "en");
            //TODO remove comment HomeActivity.rtl = viewId == R.id.btn_lang_arabic;
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
