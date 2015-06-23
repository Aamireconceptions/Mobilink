package com.ooredoo.bizstore.ui.fragments;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.DialogUtils;

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
    }

    @Override
    public void onClick(View v) {
        DialogUtils.showVerificationCodeDialog(mActivity);
    }

}
