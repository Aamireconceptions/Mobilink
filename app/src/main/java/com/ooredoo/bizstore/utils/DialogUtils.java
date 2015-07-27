package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.UpdateRatingTask;
import com.ooredoo.bizstore.ui.fragments.BaseFragment;
import com.ooredoo.bizstore.ui.fragments.SubscriptionPlansFragment;
import com.ooredoo.bizstore.ui.fragments.WelcomeFragment;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static com.ooredoo.bizstore.AppConstant.VERIFICATION_CODE_MIN_LEN;
import static com.ooredoo.bizstore.BizStore.password;
import static com.ooredoo.bizstore.utils.Converter.convertDpToPixels;
import static com.ooredoo.bizstore.utils.FragmentUtils.replaceFragmentWithBackStack;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author pehlaj.rai
 * @since 6/18/2015.
 */
public class DialogUtils {

    public static Dialog createCustomLoader(Activity activity, String txt) {

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_loader, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);

        builder.setView(view);
        Dialog dialog = builder.create();

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });

        final Animation animation = AnimationUtils.loadAnimation(activity, R.anim.rotate);
        view.findViewById(R.id.iv_loader).startAnimation(animation);
        ((TextView) view.findViewById(R.id.tv_title)).setText(txt);

        dialog.show();

        int width = (int) convertDpToPixels(200);
        int height = (int) convertDpToPixels(125);

        dialog.getWindow().setLayout(width, height);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog showRatingDialog(final Activity activity, final String type, final long typeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_rate, null);
        builder.setView(view);

        final Dialog dialog = builder.create();

        ((RatingBar) view.findViewById(R.id.rating_bar)).setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                new UpdateRatingTask(activity, type, typeId, rating).execute();
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialog.setCancelable(true);
        builder.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

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

        final View view = inflater.inflate(R.layout.dialog_verification_code, null);
        builder.setView(view);

        final Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                BaseFragment.hideKeyboard(activity);
                AppCompatActivity compatActivity = (AppCompatActivity) activity;
                replaceFragmentWithBackStack(compatActivity, R.id.fragment_container, new WelcomeFragment(), "welcome_fragment");
                //TODO un-comment & remove above 3 lines processVerificationCode();

               // processVerificationCode();
            }

            private void processVerificationCode() {
                EditText etCode = (EditText) view.findViewById(R.id.et_code);
                String code = etCode.getText().toString();
                if(isNotNullOrEmpty(code) && code.length() >= VERIFICATION_CODE_MIN_LEN && code.equals(password)) {
                    dialog.dismiss();
                    BaseFragment.hideKeyboard(activity);
                    activity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    AppCompatActivity compatActivity = (AppCompatActivity) activity;
                    replaceFragmentWithBackStack(compatActivity, R.id.fragment_container, new WelcomeFragment(), "welcome_fragment");
                } else {
                    Snackbar.make(etCode, activity.getString(R.string.error_invalid_verification_code), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setCancelable(true);
        builder.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
