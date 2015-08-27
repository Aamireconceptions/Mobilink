package com.ooredoo.bizstore.ui.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.ShareAppTask;
import com.ooredoo.bizstore.utils.SnackBarUtils;

public class ShareAppActivity extends AppCompatActivity {
    private EditText etPhoneNum;

    private SnackBarUtils snackBarUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_app);

        setupToolbar();

        init();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.share_app_with_friends));
    }

    private void init() {
        etPhoneNum = (EditText) findViewById(R.id.phone_number);

        snackBarUtils = new SnackBarUtils(this, etPhoneNum);
    }

    public void shareApp(View v) {
        String phoneNum = etPhoneNum.getText().toString().trim();

        if(!phoneNum.isEmpty() && phoneNum.length() >= AppConstant.MSISDN_MIN_LEN) {
            phoneNum = "+974" + phoneNum;

            ShareAppTask shareAppTask = new ShareAppTask(this, snackBarUtils);
            shareAppTask.execute(phoneNum);
        } else {
            snackBarUtils.showSimple(R.string.error_provide_num, Snackbar.LENGTH_SHORT);
        }
    }
}