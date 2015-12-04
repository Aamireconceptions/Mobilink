package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

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

       // dialog = DialogUtils.createCustomLoader((Activity) activity, "Verifying....");
       // dialog.show();;
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

       // dialog.dismiss();

        if(result != null)
        {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            if(response.resultCode != -1)
            {
                if(response.desc.equals("Logged in successfully"))
                {
                    DialogUtils.startWelcomeFragment();
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
        params.put("password", BizStore.password);
        //params.put("secret", "A33w3zH2OsCMD");

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_URL + query);

        Logger.print("login() URL:" + url.toString());

        result = getJson(url);

        Logger.print("Login result:" + result);

        return result;
    }
}