package com.ooredoo.bizstore.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.SplashFragment;
import com.ooredoo.bizstore.ui.fragments.SubscriptionPlansFragment;
import com.ooredoo.bizstore.utils.FragmentUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.getBooleanVal;

public class MainActivity extends BaseActivity {
    public static boolean hideToolbar = false;

    public MainActivity() {
        super();
        layoutResId = R.layout.activity_main;
    }

    boolean isSplashShowing = true;
    WebView webView;
    @Override
    public void init() {

        webView = (WebView) findViewById(R.id.gif_view);
        webView.loadUrl("file:///android_asset/gif.html");
        setupToolbar();
        showSplash();
    }

    public Toolbar toolbar;
    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        setTitle("");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.drawable.ic_bizstore);
    }

    private void showSplash()
    {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideSplash();
                    }
                });

            }
        }, 4000);
    }

    private void hideSplash()
    {
        finish();

        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

       /* boolean check = getBooleanVal(this, SharedPrefUtils.LOGIN_STATUS);
        if(check) {
            startActivity(HomeActivity.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else
        {
            FragmentUtils.replaceFragmentAllowStateLoseAnimation(this, R.id.fragment_container,
                    new SubscriptionPlansFragment(), "subscription_plan_fragment");

            startActivity(new Intent(this, HomeActivity.class));

            isSplashShowing = false;
        }*/

        //webView.clearCache(true);

    }

    @Override
    public void onBackPressed() {
        if(isSplashShowing)
        {
            Logger.print("MAIN: return splash showing");

            return;
        }

        if(getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack();

            Logger.print("MAIN: return from pop");
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webView.destroy();
    }
}