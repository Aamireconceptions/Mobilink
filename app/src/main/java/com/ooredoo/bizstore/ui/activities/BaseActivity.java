package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.utils.Logger;

import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;

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

        String languageToLoad;
        if(savedInstanceState != null)
        {
            languageToLoad = savedInstanceState.getString("lang");
        }
        else
        {
           languageToLoad = BizStore.getLanguage();
        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        BizStore.setLanguage(languageToLoad);

        setContentView(layoutResId);
        BizStore bizStore = (BizStore) getApplicationContext();
        bizStore.overrideDefaultFonts();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Logger.print("Putting in:"+BizStore.getLanguage());
        outState.putString("lang", BizStore.getLanguage());
    }
}