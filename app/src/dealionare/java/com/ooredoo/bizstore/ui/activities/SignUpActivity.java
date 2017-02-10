package com.ooredoo.bizstore.ui.activities;

import android.app.FragmentManager;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.DemoFragment;

import static com.ooredoo.bizstore.utils.FragmentUtils.addFragmentWithBackStack;

public class SignUpActivity extends BaseActivity {

    public static boolean hideToolbar = false;

    FragmentManager mFragmentManager;

    public SignUpActivity() {
        super();
        layoutResId = R.layout.activity_sign_up;
    }

    @Override
    public void init() {
        //BASE_URL = getAppUrl(this);
        setupToolbar();
        mFragmentManager = getFragmentManager();

        if(BuildConfig.FLAVOR.equals("ooredoo")) {
            addFragmentWithBackStack(this, R.id.fragment_container, new SignInFragment(), "signin_frag");
        }
        else
        {
            addFragmentWithBackStack(this, R.id.fragment_container, new DemoFragment(), "demo_fragment");
        }
       /* Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            boolean isSignin = bundle.getBoolean("is_signin");

            if(isSignin)
            {
                SignUpFragment signUpFragment = new SignUpFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("is_signin", isSignin);
                signUpFragment.setArguments(bundle1);
                addFragmentWithBackStack(this, R.id.fragment_container, signUpFragment, "signup_fragment");
            }
        }
        else
        addFragmentWithBackStack(this, R.id.fragment_container, new DemoFragment(), "demo_fragment");*/
    }

    public Toolbar toolbar;
    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(BuildConfig.FLAVOR.equals("telenor") || BuildConfig.FLAVOR.equals("dealionare"))
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.red));
        }

        if(BuildConfig.FLAVOR.equals("dealionare"))
        {
            toolbar.setBackgroundColor(Color.parseColor("#fb9900"));
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mFragmentManager.getBackStackEntryCount() > 1) {
                mFragmentManager.popBackStack();
                toggleToolbar();
                return true;
            } else {
                startActivity(MainActivity.class);
            }
        }*/
        return super.onKeyDown(keyCode, event);
    }

    private void toggleToolbar() {
        toolbar.setVisibility(hideToolbar ? View.GONE : View.VISIBLE);
        hideToolbar = false;
    }


}