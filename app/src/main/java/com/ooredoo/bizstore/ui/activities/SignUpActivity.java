package com.ooredoo.bizstore.ui.activities;

import android.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.SignUpFragment;

import static com.ooredoo.bizstore.utils.FragmentUtils.addFragmentWithBackStack;

public class SignUpActivity extends BaseActivity {

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
        addFragmentWithBackStack(this, R.id.fragment_container, new SignUpFragment(), "SignUp");
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mFragmentManager.getBackStackEntryCount() > 1) {
                mFragmentManager.popBackStack();
                return true;
            } else {
                startActivity(MainActivity.class);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
