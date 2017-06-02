package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.dialogs.MsisdnDialog;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class LoginTask extends BaseAsyncTask<String, Void, String> {

    private Dialog dialog;

    private final static String SERVICE_URL= "/login?";
    private Context activity;

    public LoginTask(Context activity) {

        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader((Activity) activity, "Logging In....");
        dialog.show();
    }

    String msisdn;
    @Override
    protected String doInBackground(String... params) {
        try {
            msisdn = params[0];

            return login(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

       if(dialog != null)
       {
           dialog.dismiss();
       }

        if(result != null)
        {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            if(response.resultCode != -1)
            {
                if(response.resultCode == 0)
                {
                    if(response.desc.equals("Invalid Pincode"))
                    {
                        DialogUtils.createAlertDialog(activity, 0, R.string.invalid_password).show();

                        return;
                    }
                }
                if(response.resultCode == 500)
                {
                    DialogUtils.createAlertDialog(activity, 0, R.string.error_server_down).show();

                    return;
                }

                if(response.desc.equals("Logged in successfully"))
                {
                    BizStore.username = msisdn;

                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(activity);
                    sharedPrefUtils.updateVal((Activity) activity, "username", BizStore.username);
                    sharedPrefUtils.updateVal((Activity) activity, "password", BizStore.password);

                    response.apiToken = BizStore.password;

                    BizStore.secret = response.apiToken;

                    sharedPrefUtils.updateVal((Activity) activity, "secret", response.apiToken);

                    DialogUtils.activity = (Activity) activity;

                    DialogUtils.dialog.dismiss();

                    DealDetailActivity.dialog.dismiss();

                    MsisdnDialog.chargesDialog.dismiss();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        DealDetailActivity.btGetCode.callOnClick();
                    }
                    else
                    {
                        DealDetailActivity.btGetCode.performClick();
                    }

                    Tracker tracker = ((BizStore) ((Activity) activity).getApplication()).getDefaultTracker();
                    Map<String, String> loginEvent = new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Log in")
                            .build();

                    tracker.send(loginEvent);
                }
                else
                if(response.desc.equals("password msg is sent to user"))
                {
                    BizStore.password = CryptoUtils.encryptToAES(response.password);

                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(activity);
                    sharedPrefUtils.updateVal((Activity) activity, "username", BizStore.username);
                    sharedPrefUtils.updateVal((Activity) activity, "password", BizStore.password);

                    DialogUtils.activity = (Activity) activity;

                    DialogUtils.startWelcomeFragment();
                }

                if(response.resultCode == 4)
                {
                    if(response.desc.equals("Not Billed"))
                    {
                        DialogUtils.createAlertDialog(activity, 0, R.string.error_insufficient_balance).show();
                    }
                }
                else
                    if(response.resultCode == 1)
                    {
                        DialogUtils.createAlertDialog(activity, 0, R.string.error_signin_failure_ufone).show();
                    }
            }
            else
            {
                DialogUtils.dismissPasswordDialog();

                DialogUtils.createAlertDialog(activity, 0, 0).show();
            }
        }
        else
        {
            Toast.makeText(activity, "Please make sure you are connected to internet and try again!", Toast.LENGTH_LONG).show();
        }
    }

    private String login(String msisdn) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(MSISDN, msisdn);
        params.put("password",  BizStore.password);

        String query = createQuery(params);

         URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_URL + query);

        Logger.print("login() URL:" + url.toString());

        result = getJson(url);

        Logger.print("Login result:" + result);

        return result;
    }

}