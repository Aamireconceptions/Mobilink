package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.LoginTask;
import com.ooredoo.bizstore.asynctasks.UnSubTask;
import com.ooredoo.bizstore.asynctasks.UpdateRatingTask;
import com.ooredoo.bizstore.ui.fragments.BaseFragment;
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

        Button btCancel = (Button) view.findViewById(R.id.btn_cancel);
        FontUtils.setFont(activity, btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btUnSub = (Button) view.findViewById(R.id.btn_unsub);
        FontUtils.setFont(activity, btUnSub);
        btUnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UnSubTask(activity).execute(BizStore.username);
                dialog.dismiss();
            }
        });

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        FontUtils.setFontWithStyle(activity, tvTitle, Typeface.BOLD);

        dialog.setCancelable(true);
        builder.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    static Activity activity;

    public static EditText etCode;
    static Dialog dialog;
    public static ProgressBar progressBar;
    public static void showVerificationCodeDialog(final Activity activity) {

        DialogUtils.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();



        final View view = inflater.inflate(R.layout.dialog_verification_code, null);

        etCode = (EditText) view.findViewById(R.id.et_code);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        builder.setView(view);

        dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // dialog.dismiss();
                //BaseFragment.hideKeyboard(activity);
                //AppCompatActivity compatActivity = (AppCompatActivity) activity;
                //replaceFragmentWithBackStack(compatActivity, R.id.fragment_container, new WelcomeFragment(), "welcome_fragment");
                //TODO un-comment & remove above 3 lines processVerificationCode();

                processVerificationCode();
            }


        });

        dialog.setCancelable(true);
        //builder.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    static LoginTask loginTask = new LoginTask(null);
    public static void processVerificationCode() {


       // etCode.setText(BizStore.password);

        String code = etCode.getText().toString().trim();

        if(!code.isEmpty())
        {
            BizStore.password = code;
        }

        if(isNotNullOrEmpty(code) && code.length() >= VERIFICATION_CODE_MIN_LEN && code.equals(password)) {

           /* SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(activity);
            sharedPrefUtils.updateVal(activity, "username", BizStore.username);
            sharedPrefUtils.updateVal(activity, "password", BizStore.password);*/


            if(loginTask.getStatus() != AsyncTask.Status.RUNNING)
            {
                loginTask = new LoginTask(activity);
                loginTask.execute();
            }
                    /*dialog.dismiss();
                    BaseFragment.hideKeyboard(activity);
                    activity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    AppCompatActivity compatActivity = (AppCompatActivity) activity;
                    replaceFragmentWithBackStack(compatActivity, R.id.fragment_container, new WelcomeFragment(), "welcome_fragment");*/
        } else {
            Snackbar.make(etCode, activity.getString(R.string.error_invalid_verification_code), Snackbar.LENGTH_SHORT).show();
        }
    }

    public static void dismissPasswordDialog()
    {
        dialog.dismiss();
    }

    public static void startWelcomeFragment()
    {
        dialog.dismiss();
         BaseFragment.hideKeyboard(activity);
        activity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppCompatActivity compatActivity = (AppCompatActivity) activity;
        FragmentUtils.replaceFragmentWithBackStack(compatActivity, R.id.fragment_container, new WelcomeFragment(), "welcome_fragment");
    }

    public static Dialog createAlertDialog(Context context, int titleResId, int infoResId)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_dialog, null);

        if(titleResId != 0)
        {
            TextView textView = (TextView) view.findViewById(R.id.title);
            textView.setText(titleResId);
            textView.setVisibility(View.VISIBLE);
        }

        if(infoResId != 0)
        {
            TextView textView = (TextView) view.findViewById(R.id.info);
            textView.setText(infoResId);
        }


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
        dialog.setContentView(view);
        //dialog.setContentView(R.layout.alert_dialog);


        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static Dialog createLocationDialog(final Context context)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
        dialog.setContentView(R.layout.location_dialog);


        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                context.startActivity(intent);
            }
        });

        return dialog;
    }
}
