package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.ooredoo.bizstore.R;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class HelpActivity extends BaseActivity {
    public HelpActivity() {
        super();
        layoutResId = R.layout.activity_help;
    }

    @Override
    public void init() {
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}