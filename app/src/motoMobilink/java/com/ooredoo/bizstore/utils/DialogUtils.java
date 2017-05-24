package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.LoginTask;
import com.ooredoo.bizstore.asynctasks.UnSubTask;
import com.ooredoo.bizstore.asynctasks.UpdateRatingTask;
import com.ooredoo.bizstore.dialogs.ChargesDialog;
import com.ooredoo.bizstore.ui.fragments.BaseFragment;
import com.ooredoo.bizstore.ui.fragments.WelcomeFragment;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static com.ooredoo.bizstore.AppConstant.VERIFICATION_CODE_MIN_LEN;
import static com.ooredoo.bizstore.utils.Converter.convertDpToPixels;
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

        jdbUnsubscribe(activity);
    }

    private static void jdbUnsubscribe(final Activity activity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.unsub_dialog);

        builder.setMessage("Are you sure, you want to unsubscribe?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new UnSubTask(activity).execute(BizStore.username, BizStore.password);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });


        builder.setCancelable(true);
        dialog = builder.create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static Activity activity;

    public static EditText etCode;
    public static Dialog dialog;
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

                processVerificationCode();
            }


        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    static LoginTask loginTask = new LoginTask(null);
    public static void processVerificationCode() {

        String code = etCode.getText().toString().trim();

        if(!code.isEmpty())
        {
            BizStore.password = code;
        }

        if(isNotNullOrEmpty(code) && code.length() >= VERIFICATION_CODE_MIN_LEN ) {

            if(loginTask.getStatus() != AsyncTask.Status.RUNNING)
            {
                loginTask = new LoginTask(activity);
                loginTask.execute(ChargesDialog.msisdn);
            }
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
        if(dialog != null)dialog.dismiss();
        BaseFragment.hideKeyboard(activity);
        activity.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppCompatActivity compatActivity = (AppCompatActivity) activity;

        Fragment fragment = new WelcomeFragment();

        FragmentUtils.replaceFragmentWithBackStack(compatActivity, R.id.fragment_container,
                fragment, "welcome_fragment");
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
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
        dialog.setContentView(view);
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static Dialog createFOCAlertDialog(final Context context, int titleResId, int infoResId)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.foc_alert_dialog, null);

        TextView tvTos = (TextView) view.findViewById(R.id.tos);
        tvTos.setText(Html.fromHtml(context.getString(R.string.subscription_terms_services)));
        tvTos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                String uriString = BizStore.getLanguage().equals("en")
                        ? "http://ooredoo.bizstore.com.pk/index.php/other/terms/android?language=en"
                        : "http://ooredoo.bizstore.com.pk/index.php/other/terms/android?language=ar";

                intent.setData(Uri.parse(uriString));

                context.startActivity(intent);
            }
        });

        tvTos.setVisibility(View.GONE);

        if(titleResId != 0)
        {
            TextView textView = (TextView) view.findViewById(R.id.title);
            textView.setText(titleResId);
            textView.setVisibility(View.VISIBLE);
        }

        if(infoResId != 0)
        {
            TextView textView = (TextView) view.findViewById(R.id.info);
            textView.setText(Html.fromHtml(context.getString(infoResId)));
        }

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
        dialog.setContentView(view);

        return dialog;
    }

    static boolean goingForLoc = false;
    public static Dialog createLocationDialog(final Context context, String str)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
        dialog.setContentView(R.layout.location_dialog);

        if(str != null) {
            TextView tvNote = (TextView) dialog.findViewById(R.id.location_note);
            tvNote.setText(str);
        }

        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goingForLoc = true;
                dialog.dismiss();

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                context.startActivity(intent);
            }
        });

        return dialog;
    }

    public static Dialog createMobilinkRedeemDialog(Context context)
    {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_mobilink_redeem);

        return dialog;
    }

    public static void showRateUsDialog(final Context context)
    {
        boolean canShow = SharedPrefUtils.canShowRate((Activity) context, "can_show_rate");

        if(canShow) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.unsub_dialog);
            builder.setMessage("Enjoyed discount, let's rate us on play store.");
            builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPrefUtils.updateVal((Activity) context, "can_show_rate", false);

                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
                    context.startActivity(intent);

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.setNeutralButton("Don't ask again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPrefUtils.updateVal((Activity) context, "can_show_rate", false);
                }
            });

            builder.show();
        }
    }

    public static AlertDialog.Builder createRationaleDialog(final Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.unsub_dialog);
        builder.setTitle(title);
        builder.setMessage(message);

        return builder;
    }


}
