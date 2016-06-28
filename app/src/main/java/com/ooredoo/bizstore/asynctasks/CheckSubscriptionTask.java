package com.ooredoo.bizstore.asynctasks;

import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.PASSWORD;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class CheckSubscriptionTask extends BaseAsyncTask<Void, Void, String>
{

    private final static String SERVICE_NAME = "/verify?";

    private HomeActivity activity;

    private Timer timer;

    private TimerTask timerTask;

    public CheckSubscriptionTask(HomeActivity activity, Timer timer, TimerTask timerTask)
    {
        this.activity = activity;

        this.timer = timer;

        this.timerTask = timerTask;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try
        {
            return check();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        if(result != null) {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            if (response.desc.equals("Invalid MSIDN/Password")
                    ||
                    (BuildConfig.FLAVOR.equals("ufone") && !response.desc.equals("Active")))
            {
                Toast.makeText(activity, R.string.unsub_from_service, Toast.LENGTH_LONG).show();

                timerTask.cancel();
                timer.cancel();

                updateVal(activity, LOGIN_STATUS, false);
                activity.finish();
                activity.startActivity(new Intent(activity, MainActivity.class));
            }

        }
    }

    private String check() throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(MSISDN, BizStore.username);
        params.put("password", BizStore.password);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        if(BuildConfig.FLAVOR.equals("ufone"))
        {
            params.clear();

            params.put(MSISDN, BizStore.username);
            params.put("password", "ZwRq5CsY96w3zCMD");

            query = createQuery(params);

            url = new URL("http://203.215.183.98:30119/yellowPages2/mobileAppSupport/checkUserSubscription?"+query);
        }

        Logger.print("check Subscription URL:" + url.toString());

        result = getJson(url);

        Logger.print("check: " + result);

        return result;
    }

}