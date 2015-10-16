package com.ooredoo.bizstore.asynctasks;

import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class CheckSubscriptionTask extends BaseAsyncTask<Void, Void, String>
{

    private final static String SERVICE_NAME = "/verify?";

    private HomeActivity activity;

    public CheckSubscriptionTask(HomeActivity activity)
    {
        this.activity = activity;
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

            if (response.desc.equals("Invalid MSIDN/Password"))
            {
                Toast.makeText(activity, "You have been Unsubscribed from the service", Toast.LENGTH_LONG).show();

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

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("check Subscription URL:" + url.toString());

        result = getJson(url);

        Logger.print("check: " + result);

        return result;
    }

}