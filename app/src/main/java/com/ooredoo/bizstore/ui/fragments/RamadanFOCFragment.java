package com.ooredoo.bizstore.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.FragmentUtils;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class RamadanFOCFragment extends BaseFragment {

    Button btnSubscribe, btLogin;

    public RamadanFOCFragment() {
        super();
        layoutResId = R.layout.fragment_ramadan_foc;
    }

    public void init(View parent) {
        SignUpActivity signUpActivity = (SignUpActivity) mActivity;
        signUpActivity.toolbar.setVisibility(View.GONE);
        SignUpActivity.hideToolbar = true;
        btnSubscribe = (Button) parent.findViewById(R.id.btn_subscribe);
        btnSubscribe.setOnClickListener(this);

     //   btLogin = (Button) parent.findViewById(R.id.btn_login);
     //   btLogin.setOnClickListener(this);

        TextView tvTOS = (TextView ) parent.findViewById(R.id.agree_terms_services);
        tvTOS.setText(Html.fromHtml(signUpActivity.getString(R.string.subscription_terms_services)));
        tvTOS.setOnClickListener(this);


        TextView tvEnjoy = (TextView) view.findViewById(R.id.enjoy);
        TextView tv1Week = (TextView) view.findViewById(R.id._1_week);
        TextView tvFree = (TextView) view.findViewById(R.id.free);

        FontUtils.setFontWithStyle(getActivity(), tvEnjoy, Typeface.BOLD);
        FontUtils.setFontWithStyle(getActivity(), tv1Week, Typeface.BOLD);
        FontUtils.setFontWithStyle(getActivity(), tvFree, Typeface.BOLD);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_subscribe) {
            /*updateVal(mActivity, LOGIN_STATUS, true);
            SignUpActivity activity = (SignUpActivity) mActivity;
            activity.startActivity(HomeActivity.class);*/

            AppCompatActivity compatActivity = (AppCompatActivity) mActivity;
            FragmentUtils.replaceFragmentWithBackStack(compatActivity, R.id.fragment_container,
                                                       new SignUpFragment(), "SignUp");
        }

       /* if (id == R.id.btn_login) {

            Bundle bundle = new Bundle();
            bundle.putBoolean("login", true);

            SignUpFragment signUpFragment = new SignUpFragment();
            signUpFragment.setArguments(bundle);

            FragmentUtils.replaceFragmentWithBackStack((AppCompatActivity) mActivity, R.id.fragment_container,
                   signUpFragment, "LogIn");
        }*/

        if(id == R.id.agree_terms_services)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            String uriString = BizStore.getLanguage().equals("en")
                    ? "http://ooredoo.bizstore.com.pk/index.php/other/terms/android?language=en"
                    : "http://ooredoo.bizstore.com.pk/index.php/other/terms/android?language=ar";

            intent.setData(Uri.parse(uriString));

            startActivity(intent);

        }
    }
}