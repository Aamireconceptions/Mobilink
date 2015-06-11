package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Pehlaj Rai
 * @since 6/11/2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {
    protected int layoutResId;

    public BaseActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
        init();
    }

    public abstract void init();

    public void startActivity(Class cls) {
        Intent i = new Intent(this, cls);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View v) {
    }

}