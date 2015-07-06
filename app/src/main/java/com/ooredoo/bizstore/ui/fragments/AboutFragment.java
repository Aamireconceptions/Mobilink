package com.ooredoo.bizstore.ui.fragments;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.FragmentUtils;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public class AboutFragment extends BaseFragment {

    public AboutFragment() {
        super();
        layoutResId = R.layout.fragment_sign_up;
    }

    public void init(View parent) {
        parent.findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        showVerificationCodeDialog();
    }

    public void showVerificationCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_verification_code, null);
        builder.setView(view);

        final Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                hideKeyboard(mActivity);
                AppCompatActivity activity = (AppCompatActivity) mActivity;
                FragmentUtils.replaceFragmentWithBackStack(activity, R.id.fragment_container, new WelcomeFragment(), "");
            }
        });

        dialog.setCancelable(true);
        builder.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
