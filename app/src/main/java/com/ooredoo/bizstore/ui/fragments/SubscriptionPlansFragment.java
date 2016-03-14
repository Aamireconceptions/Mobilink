package com.ooredoo.bizstore.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FragmentUtils;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class SubscriptionPlansFragment extends BaseFragment {

    Button btnSubscribe;

    public SubscriptionPlansFragment() {
        super();
        layoutResId = R.layout.fragment_subscription_plans;
    }

    public void init(View parent) {
        SignUpActivity signUpActivity = (SignUpActivity) mActivity;
        signUpActivity.toolbar.setVisibility(View.GONE);
        SignUpActivity.hideToolbar = true;
        btnSubscribe = (Button) parent.findViewById(R.id.btn_subscribe);
        btnSubscribe.setOnClickListener(this);

        TextView tvTOS = (TextView ) parent.findViewById(R.id.agree_terms_services);
        tvTOS.setText(Html.fromHtml(signUpActivity.getString(R.string.subscription_terms_services)));
        tvTOS.setOnClickListener(this);

        if(BuildConfig.FLAVOR.equals("telenor")){
            tvTOS.setVisibility(View.GONE);
        }
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

        if(id == R.id.agree_terms_services)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://ooredoo.bizstore.com.pk/index.php/other/terms/android"));

            startActivity(intent);

        }
    }
}
