package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.fragments.WelcomeFragment;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static com.ooredoo.bizstore.utils.FragmentUtils.replaceFragmentWithBackStack;

/**
 * @author pehlaj.rai
 * @since 6/18/2015.
 */
public class DialogUtils {

    public static void showUnSubscribeDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_unsub, null);
        builder.setView(view);

        final Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_unsub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO unsubscribe
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        builder.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showVerificationCodeDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_verification_code, null);
        builder.setView(view);

        final Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                AppCompatActivity compatActivity = (AppCompatActivity) activity;
                replaceFragmentWithBackStack(compatActivity, R.id.fragment_container, new WelcomeFragment(), "WELCOME_FRAGMENT");
            }
        });

        dialog.setCancelable(true);
        builder.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
