package com.ooredoo.bizstore.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.FOCTask;
import com.ooredoo.bizstore.asynctasks.LoginTask;
import com.ooredoo.bizstore.asynctasks.SignInTask;
import com.ooredoo.bizstore.asynctasks.SubscriptionTask;
import com.ooredoo.bizstore.model.Subscription;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NetworkUtils;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
import static com.ooredoo.bizstore.AppConstant.MSISDN_MIN_LEN;
import static com.ooredoo.bizstore.utils.DialogUtils.showVerificationCodeDialog;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class SignUpFragment extends BaseFragment {

    EditText etMsisdn;

    boolean isSignin;

    public boolean login, checkForFOC = true;
    public SignUpFragment() {
        super();
        layoutResId = R.layout.fragment_sign_up;
    }

    public void init(View parent) {
        SignUpActivity.hideToolbar = false;
        SignUpActivity signUpActivity = (SignUpActivity) mActivity;
        signUpActivity.toolbar.setVisibility(View.VISIBLE);

        EditText etCountryCode = (EditText) parent.findViewById(R.id.et_country_code);
        FontUtils.setFont(getActivity(), etCountryCode);

        etMsisdn = (EditText) parent.findViewById(R.id.et_phone_num);
        etMsisdn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0 && s.toString().charAt(0) == '0') {
                    String subString = s.toString().substring(1);

                    etMsisdn.setText(subString);
                }

            }
        });
        Button btNext = (Button) parent.findViewById(R.id.btn_next);
        btNext.setOnClickListener(this);

        mActivity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_VISIBLE | SOFT_INPUT_ADJUST_RESIZE);

        if(BuildConfig.FLAVOR.equals("dealionare"))
        {
            parent.findViewById(R.id.tv_sms_charges).setVisibility(View.GONE);
        }

        Bundle bundle = getArguments();
        if(bundle != null) {
            isSignin = bundle.getBoolean("is_signin");
        }

    }

    @Override
    public void onClick(View v) {
        //showVerificationCodeDialog(mActivity);
        //TODO uncomment & remove above line subscribe();

        subscribe();
    }

    private void subscribe() {
        String msisdn = etMsisdn.getText().toString();
       // msisdn = "66703202";

        /*if(!msisdn.equals("66703202"))
        {
            Snackbar.make(etMsisdn, mActivity.getString(R.string.invalid_num), Snackbar.LENGTH_SHORT).show();

            return;
        }*/

        String errMsg = "Error";
        if(NetworkUtils.hasInternetConnection(mActivity)) {
            if(isNotNullOrEmpty(msisdn) && msisdn.length() >= MSISDN_MIN_LEN) {
                BizStore.username = msisdn;

                if(BuildConfig.FLAVOR.equals("dealionare") || login)
                {
                    new LoginTask(mActivity).execute();
                }
                else
                {
                    if(isSignin)
                    {
                        new SignInTask(this).execute(msisdn);
                    }
                    else {
                        if(checkForFOC)
                        {
                            new FOCTask(this).execute(msisdn);
                        }
                        else
                        {
                            new SubscriptionTask(this).execute(msisdn);
                        }
                    }
                }

            } else {
                errMsg = getString(R.string.error_invalid_num);
            }
        } else {
            errMsg = getString(R.string.error_no_internet);
        }

        if(!errMsg.equals("Error")) {
            Snackbar.make(etMsisdn, errMsg, Snackbar.LENGTH_LONG).show();
        }
    }

    public void processSubscription(Subscription subscription) {
        String errMsg = "Error";
        if(subscription != null) {
            if(subscription.resultCode != -1) {

               // BizStore.password = subscription.password;

                showVerificationCodeDialog(mActivity);

               // DialogUtils.etCode.setText(subscription.password);


            } else {
                errMsg = subscription.desc;
            }
        } else {
            errMsg = getString(R.string.error_server_down);
        }

        if(!errMsg.equals("Error")) {
            Snackbar.make(etMsisdn, errMsg, Snackbar.LENGTH_LONG).show();
        }
    }
}