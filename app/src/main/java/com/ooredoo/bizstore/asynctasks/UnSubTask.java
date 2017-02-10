package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Pehlaj Rai
 * @since 14-Jul-15
 */
public class UnSubTask extends BaseAsyncTask<String, Void, String> {

    private Activity activity;

    private Dialog dialog;

    private static final String SERVICE_NAME = "/signout?";
    public UnSubTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogUtils.createCustomLoader(activity, activity.getString(R.string.unsubscribing));
    }

    @Override
    protected String doInBackground(String... params) {
        String msisdn = params[0];
        String password = params[1];
        try {
            return unSubscribe(msisdn, password);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        closeDialog(dialog);

        if(result != null) {
            updateVal(activity, LOGIN_STATUS, false);

            activity.finish();

            activity.startActivity(new Intent(activity, MainActivity.class));

            BizStore bizStore = (BizStore) activity.getApplication();
            Tracker tracker = bizStore.getDefaultTracker();

            Map<String, String> unsubEvent = new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Unsubscribe")
                    .build();

            tracker.send(unsubEvent);

            if(BuildConfig.FLAVOR.equals("ooredoo"))
            {
                Tracker ooredooTracker = bizStore.getOoredooTracker();
                ooredooTracker.send(unsubEvent);
            }

            Toast.makeText(activity, R.string.un_sub_success, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(activity, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Un-Subscribe user, invalidate user session etc & close app
     *
     * @param msisdn
     * @param password
     * @throws IOException
     */
    private String unSubscribe(String msisdn, String password) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        //params.put("pincode", CryptoUtils.encodeToBase64(CryptoUtils.decryptAES(password)));

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        if(BuildConfig.FLAVOR.equals("ufone"))
        {
            params.clear();
            params.put(MSISDN, BizStore.username);
            params.put("password", "ZwRq5CsY96w3zCMD");

            query = createQuery(params);

            url = new URL("http://203.215.183.98:30119/yellowPages2/mobileAppSupport/logout?" + query);
        }

        Logger.print("UnSub URL: "+url.toString());

        result = getJson(url);

        Logger.print("UnSubscribe: " + result);

        return result;
    }
}