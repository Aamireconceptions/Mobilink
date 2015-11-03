package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Babar on 03-Nov-15.
 */
public class DealDetailMiscTask extends BaseAsyncTask<String, Void, String>
{
    private DealDetailActivity activity;

    private List<GenericDeal> similarDeals, nearbyDeals;

    private final static String SERVICE_NAME = "/deal_misc";

    public DealDetailMiscTask(DealDetailActivity activity, List<GenericDeal> similarDeals, List<GenericDeal> nearbyDeals)
    {
        this.activity = activity;

        this.similarDeals = similarDeals;

        this.nearbyDeals = nearbyDeals;
    }

    @Override
    protected String doInBackground(String... params)
    {
        return null;
    }

    @Override
    protected void onPostExecute(String result) throws JsonSyntaxException
    {
        super.onPostExecute(result);

        if(result != null)
        {
            Gson gson = new Gson();

            DealMisc dealMisc = gson.fromJson(result, DealMisc.class);

            similarDeals = dealMisc.similarDeals;
            nearbyDeals = dealMisc.nearbyDeals;

            activity.onHaveData();
        }
        else
        {
            activity.onNoData();

            Toast.makeText(activity, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private String getDealsMisc(String id, String category) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(ID, id);
        params.put(CATEGORY, category);
        params.put("lat", String.valueOf(HomeActivity.lat));
        params.put("lng", String.valueOf(HomeActivity.lng));

        String query = createQuery(params);

        URL url = new URL(BaseAsyncTask.BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        String json = getJson(url);

        Logger.print("getDealsMisc: " + json);

        return json;
    }
}