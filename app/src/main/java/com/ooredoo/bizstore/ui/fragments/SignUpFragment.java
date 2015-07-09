package com.ooredoo.bizstore.ui.fragments;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.ooredoo.bizstore.R;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
import static com.ooredoo.bizstore.AppConstant.MSISDN_ERR_MSG;
import static com.ooredoo.bizstore.AppConstant.MSISDN_MIN_LEN;
import static com.ooredoo.bizstore.utils.DialogUtils.showVerificationCodeDialog;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class SignUpFragment extends BaseFragment {

    public SignUpFragment() {
        super();
        layoutResId = R.layout.fragment_sign_up;
    }

    public void init(View parent) {
        parent.findViewById(R.id.btn_next).setOnClickListener(this);
        mActivity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_VISIBLE | SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onClick(View v) {
        EditText etUserName = (EditText) view.findViewById(R.id.et_phone_num);
        String msisdn = etUserName.getText().toString();

        if(isNotNullOrEmpty(msisdn) && msisdn.length() >= MSISDN_MIN_LEN) {
            showVerificationCodeDialog(mActivity);
        } else {
            Snackbar.make(etUserName, MSISDN_ERR_MSG, Snackbar.LENGTH_SHORT).show();
        }
    }

}
