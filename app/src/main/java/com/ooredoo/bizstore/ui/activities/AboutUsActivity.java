package com.ooredoo.bizstore.ui.activities;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.FontUtils;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class AboutUsActivity extends BaseActivity {
    public AboutUsActivity() {
        super();
        layoutResId = R.layout.activity_about_us;
    }

    @Override
    public void init() {
        setupToolbar();

        TextView tvVersion = (TextView) findViewById(R.id.tv_app_version);
        FontUtils.setFontWithStyle(this, tvVersion, Typeface.BOLD);

        try {
            tvVersion.setText(getString(R.string.version) +" " +getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.about);
    }
}