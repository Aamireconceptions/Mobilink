package com.ooredoo.bizstore.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

import android.view.View;
import android.widget.Button;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.ui.fragments.SplashFragment;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.APP_LANGUAGE;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getBooleanVal;

public class MainActivity extends BaseActivity {


    public MainActivity() {
        super();
        layoutResId = R.layout.activity_main;
    }

    boolean isSplashShowing = true;

    @Override
    public void init() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isSplashShowing = false;
                        finish();
                        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                    }
                });

            }
        }, 2000);
    }


    @Override
    public void onBackPressed() {
        if(!isSplashShowing)
        super.onBackPressed();
    }
}
