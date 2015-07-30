package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.utils.ScrollViewHelper;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class HelpActivity extends BaseActivity {

    ActionBar mActionBar;
    public HelpActivity() {
        super();
        layoutResId = R.layout.activity_help;
    }

    @Override
    public void init() {
        setupToolbar();
        ScrollViewHelper scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);
        scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setShowHideAnimationEnabled(false);
        mActionBar.setTitle(getString(R.string.help));
    }
}