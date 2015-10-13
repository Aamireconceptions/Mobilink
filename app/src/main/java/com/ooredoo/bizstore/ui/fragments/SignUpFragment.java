package com.ooredoo.bizstore.ui.fragments;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.SubscriptionTask;
import com.ooredoo.bizstore.model.Subscription;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
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
    public SignUpFragment() {
        super();
        layoutResId = R.layout.fragment_sign_up;
    }

    public void init(View parent) {
        SignUpActivity.hideToolbar = false;
        SignUpActivity signUpActivity = (SignUpActivity) mActivity;
        signUpActivity.toolbar.setVisibility(View.VISIBLE);
        etMsisdn = (EditText) parent.findViewById(R.id.et_phone_num);
        parent.findViewById(R.id.btn_next).setOnClickListener(this);
        mActivity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_VISIBLE | SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onClick(View v) {
        //showVerificationCodeDialog(mActivity);
        //TODO uncomment & remove above line subscribe();

        subscribe();
    }

    private void subscribe() {
        String msisdn = etMsisdn.getText().toString();

        msisdn = "66703202";
        String errMsg = "Error";
        if(NetworkUtils.hasInternetConnection(mActivity)) {
            if(isNotNullOrEmpty(msisdn) && msisdn.length() >= MSISDN_MIN_LEN) {
                BizStore.username = msisdn;
                new SubscriptionTask(this).execute(msisdn);
            } else {
                errMsg = getString(R.string.error_invalid_num);
            }
        } else {
            errMsg = "Please connect to Internet.";
        }

        if(!errMsg.equals("Error")) {
            Snackbar.make(etMsisdn, errMsg, Snackbar.LENGTH_LONG).show();
        }
    }

    public void processSubscription(Subscription subscription) {
        String errMsg = "Error";
        if(subscription != null) {
            if(subscription.resultCode != -1) {

                BizStore.password = subscription.password;

                showVerificationCodeDialog(mActivity);

                DialogUtils.etCode.setText(subscription.password);
            } else {
                errMsg = subscription.desc;
            }
        } else {
            errMsg = "Error connecting to server";
        }

        if(!errMsg.equals("Error")) {
            Snackbar.make(etMsisdn, errMsg, Snackbar.LENGTH_LONG).show();
        }
    }
}