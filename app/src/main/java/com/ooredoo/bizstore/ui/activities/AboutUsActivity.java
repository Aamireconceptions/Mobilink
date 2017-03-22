package com.ooredoo.bizstore.ui.activities;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.FontUtils;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */

@EActivity
public class AboutUsActivity extends BaseActivity {
    public AboutUsActivity() {
        super();
        layoutResId = R.layout.activity_about_us;
    }

    @ViewById(R.id.tv_app_version)
    TextView tvVersion;

    @Override
    public void init() {
        setupToolbar();

        FontUtils.setFontWithStyle(this, tvVersion, Typeface.BOLD);

        try {
            tvVersion.setText(getString(R.string.version) +" " +getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @ViewById
    Toolbar toolbar;
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.about);
    }
}