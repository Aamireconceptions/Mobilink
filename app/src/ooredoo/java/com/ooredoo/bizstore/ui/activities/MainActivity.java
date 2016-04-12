package com.ooredoo.bizstore.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;

import android.view.View;
import android.widget.Button;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.ui.fragments.SplashFragment;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.APP_LANGUAGE;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getBooleanVal;

public class MainActivity extends BaseActivity {

    Button btnArabicLang, btnEnglishLang;
    public MainActivity() {
        super();
        layoutResId = R.layout.activity_main;
    }

    boolean isSplashShowing = true;

    @Override
    public void init() {
        showSplash();
        /*boolean check = getBooleanVal(this, SharedPrefUtils.LOGIN_STATUS);
        if(check) {
            startActivity(HomeActivity.class);
        }*/

        btnArabicLang = (Button) findViewById(R.id.btn_lang_arabic);

        FontUtils.setFont(this, btnArabicLang);

        btnEnglishLang = (Button) findViewById(R.id.btn_lang_english);
        btnArabicLang.setOnClickListener(this);
        btnEnglishLang.setOnClickListener(this);
        String lang = BizStore.getLanguage();

        boolean isArabicLang = StringUtils.isNotNullOrEmpty(lang) && lang.equals("ar");

        btnArabicLang.setSelected(isArabicLang);
        btnEnglishLang.setSelected(!isArabicLang);
    }

    SplashFragment splashFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private void showSplash()
    {
        splashFragment = new SplashFragment();

         fragmentManager = getFragmentManager();

         fragmentTransaction = fragmentManager.beginTransaction();
       //fragmentTransaction.setCustomAnimations(R.animator.slide_up_animator, R.animator.fade_out);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(android.R.id.content, splashFragment, null);
fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideSplash();
                    }
                });

            }
        }, 2000);
    }

    private void hideSplash()
    {
        boolean check = getBooleanVal(this, SharedPrefUtils.LOGIN_STATUS);
        if(check) {
            startActivity(HomeActivity.class);
        }
        else
        {
            // FragmentManager fragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(splashFragment);
            fragmentTransaction.commitAllowingStateLoss();

            // fragmentManager.executePendingTransactions();

            isSplashShowing = false;
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_lang_arabic || viewId == R.id.btn_lang_english) {
            boolean isArabic = viewId == R.id.btn_lang_arabic;
            String language = viewId == R.id.btn_lang_arabic ? "ar" : "en";
            SharedPrefUtils.updateVal(this, APP_LANGUAGE, language);
            BizStore.setLanguage(language);
            btnArabicLang.setSelected(isArabic);
            btnEnglishLang.setSelected(!isArabic);

            BizStore bizStore = (BizStore) getApplication();
            bizStore.overrideDefaultFonts();

            NavigationMenuOnClickListener.updateConfiguration(this, language);

            startActivity(new Intent(this, SignUpActivity.class));
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
      /*  boolean check = getBooleanVal(this, SharedPrefUtils.LOGIN_STATUS);
        if(check) {
            startActivity(HomeActivity.class);
        }*/
    }

    @Override
    public void onBackPressed() {
        if(!isSplashShowing)
        super.onBackPressed();
    }
}