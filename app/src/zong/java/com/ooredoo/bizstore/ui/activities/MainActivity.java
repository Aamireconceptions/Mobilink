package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.Logger;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;

@EActivity
public class MainActivity extends BaseActivity {
    public static boolean hideToolbar = false;
    boolean isSplashShowing = true;
    public MainActivity() {
        super();
        layoutResId = R.layout.activity_main;
    }

    @Override
    public void init() {

        showSplash();
    }

    @ViewById
    public Toolbar toolbar;
    private void setupToolbar() {

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
        }, 2000);
    }

    private void hideSplash()
    {
        finish();

        startActivity(new Intent(this, HomeActivity_.class));
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

        super.onBackPressed();
    }

}