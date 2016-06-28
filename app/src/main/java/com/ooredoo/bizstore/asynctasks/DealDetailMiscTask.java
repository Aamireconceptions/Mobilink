package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
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

    private final static String SERVICE_NAME = "/fullDetails?";

    ProgressBar progressBar;

    public DealDetailMiscTask(DealDetailActivity activity, List<GenericDeal> similarDeals,
                              List<GenericDeal> nearbyDeals, ProgressBar progressBar)
    {
        this.activity = activity;

        this.similarDeals = similarDeals;

        this.nearbyDeals = nearbyDeals;

        this.progressBar = progressBar;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try {
            return getDealsMisc(params[0], params[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) throws JsonSyntaxException
    {
        super.onPostExecute(result);

       if(progressBar != null) {
           progressBar.setVisibility(View.GONE);
       }
        if(result != null)
        {
            try {
                Gson gson = new Gson();

                DealMisc dealMisc = gson.fromJson(result, DealMisc.class);

                if(BuildConfig.FLAVOR.equals("mobilink"))
                {
                    activity.onHaveData(dealMisc.genericDeal);

                    return;
                }

                similarDeals.clear();
                nearbyDeals.clear();
                similarDeals.addAll(dealMisc.genericDeal.similarDeals);
                nearbyDeals.addAll(dealMisc.genericDeal.nearbyDeals);

                if(!BuildConfig.FLAVOR.equals("telenor") && !BuildConfig.FLAVOR.equals("ufone"))
                {
                    activity.onHaveData(dealMisc.genericDeal);
                }

            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            activity.onNoData();

           // Toast.makeText(activity, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private String getDealsMisc(String id, String businessId) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(ID, id);
        params.put("businessDetail", businessId);
        params.put("type", "deals");

        //HomeActivity.lat = 25.283982;
       // HomeActivity.lng = 51.563376;

        params.put("lat", String.valueOf(HomeActivity.lat));
        params.put("long", String.valueOf(HomeActivity.lng));

        String query = createQuery(params);

        URL url = new URL(BaseAsyncTask.BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getDealsMisc URL:"+url);

        String json = getJson(url);

        Logger.print("getDealsMisc: " + json);

        return json;
    }
}