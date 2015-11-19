package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.interfaces.LocationNotifies;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.LocationResponse;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Babar on 19-Nov-15.
 */
public class LocationsTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private Dialog dialog;

    LocationNotifies locationNotifies;

    private final static String SERVICE_NAME = "/getdetails?";

    public LocationsTask(Context context)
    {
        this.context = context;

        locationNotifies = (LocationNotifies) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader((Activity) context, context.getString(R.string.please_wait));
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getLocations(params[0], params[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);

        dialog.dismiss();

        if(s != null)
        {
            try
            {
                Gson gson = new Gson();

                LocationResponse response = gson.fromJson(s, LocationResponse.class);

                if(response.resultCode != -1)
                {
                    locationNotifies.onUpdated(response.locationData);
                }
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();

                Toast.makeText(context, R.string.error_server_down, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private String getLocations(String id, String type) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(ID,  id);
        params.put(TYPE, type);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getLocations() URL:" + url.toString());

        result = getJson(url);

        Logger.print("getLocations: " + result);

        return result;

    }
}