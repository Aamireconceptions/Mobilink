package com.ooredoo.bizstore.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.MainFragment;
import com.ooredoo.bizstore.ui.fragments.SplashFragment;
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

    @Override
    public void init() {

        setupToolbar();
        showSplash();

        FragmentUtils.replaceFragment(this, R.id.fragment_container, new MainFragment(),
        "main_fragment");

    }

    public Toolbar toolbar;
    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.red));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        setTitle("");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setLogo(R.drawable.ic_bizstore);
    }

    SplashFragment splashFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private void showSplash()
    {
        splashFragment = new SplashFragment();

        fragmentManager = getFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_up_animator, R.animator.fade_out);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(android.R.id.content, splashFragment, null);
        fragmentTransaction.commit();

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
        boolean check = getBooleanVal(this, SharedPrefUtils.LOGIN_STATUS);
        if(check) {
            startActivity(HomeActivity.class);
        }
        else
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(splashFragment);
            fragmentTransaction.commitAllowingStateLoss();

            fragmentManager.executePendingTransactions();

            isSplashShowing = false;


        }
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
}