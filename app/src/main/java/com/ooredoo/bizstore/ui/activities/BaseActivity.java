package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.ooredoo.bizstore.BizStore;

import java.util.Locale;

/**
 * @author Pehlaj Rai
 * @since 6/11/2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {

    protected Intent intent;
    protected int layoutResId;

    public BaseActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String languageToLoad  = BizStore.getLanguage(); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(layoutResId);
        intent = getIntent();
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