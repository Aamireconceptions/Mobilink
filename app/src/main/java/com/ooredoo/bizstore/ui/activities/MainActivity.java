package com.ooredoo.bizstore.ui.activities;

import android.view.View;

import com.ooredoo.bizstore.R;

public class MainActivity extends BaseActivity {

    public MainActivity() {
        super();
        layoutResId = R.layout.activity_main;
    }

    @Override
    public void init() {
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(SignUpActivity.class);
    }
}
