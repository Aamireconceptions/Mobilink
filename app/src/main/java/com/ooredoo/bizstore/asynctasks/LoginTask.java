package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class LoginTask extends BaseAsyncTask<Void, Void, String> {

    private TopBrandsStatePagerAdapter adapter;

    private ViewPager viewPager;

    private Dialog dialog;

    private final static String SERVICE_URL= "/login?";
    private Context activity;

    public LoginTask(Context activity) {

        this.activity = activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(BuildConfig.FLAVOR.equals("dealionare") || BuildConfig.FLAVOR.equals("ufone")) {
            dialog = DialogUtils.createCustomLoader((Activity) activity, "Logging In....");
            dialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return login();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

       if(dialog != null)
           dialog.dismiss();

        if(result != null)
        {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            if(response.resultCode != -1)
            {
                if(response.resultCode == 500)
                {
                    DialogUtils.createAlertDialog(activity, 0, R.string.error_server_down).show();

                    return;
                }

                if(response.desc.equals("Logged in successfully"))
                {
                    SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(activity);
                    sharedPrefUtils.updateVal((Activity) activity, "username", BizStore.username);
                    sharedPrefUtils.updateVal((Activity) activity, "password", BizStore.password);

                    DialogUtils.activity = (Activity) activity;

                    DialogUtils.startWelcomeFragment();
                }
                else
                if(response.desc.equals("password msg is sent to user"))
                {
                    BizStore.password = response.password;

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

    private String login() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        //params.put(OS, ANDROID);
        params.put(MSISDN, BizStore.username);
        if(!BuildConfig.FLAVOR.equals("dealionare")) {
            params.put("password", BizStore.password);
        }
        //params.put("secret", "A33w3zH2OsCMD");

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_URL + query);

        if(BuildConfig.FLAVOR.equals("ufone"))
        {
            params.clear();
            params.put(MSISDN, BizStore.username);
            params.put("password", "ZwRq5CsY96w3zCMD");

            query = createQuery(params);

            url = new URL("http://203.215.183.98:30119/yellowPages2/mobileAppSupport"
            + SERVICE_URL + query);
        }

        Logger.print("login() URL:" + url.toString());

        result = getJson(url);

        Logger.print("Login result:" + result);

        return result;
    }
}