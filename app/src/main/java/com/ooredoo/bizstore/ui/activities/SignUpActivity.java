package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.SignUpFragment;

import static com.ooredoo.bizstore.utils.FragmentUtils.addFragmentWithBackStack;

public class SignUpActivity extends BaseActivity {

    public SignUpActivity() {
        super();
        layoutResId = R.layout.activity_sign_up;

    }

    @Override
    public void init() {
        setupToolbar();

        addFragmentWithBackStack(this, R.id.fragment_container, new SignUpFragment(), "SignUp");
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
