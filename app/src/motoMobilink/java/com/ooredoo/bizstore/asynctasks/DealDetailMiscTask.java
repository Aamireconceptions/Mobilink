package com.ooredoo.bizstore.asynctasks;

import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/** This class will get the full details for the deals
 * Also the redeem button and redeem count of the deal
 * will only be shown after this thread finish its work
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

                if(dealMisc.genericDeal != null) {

                    activity.onHaveData(dealMisc.genericDeal);

                    return;

                    // If some day you were asked to bring back the similar
                    // and nearby deals then uncomment the code below.

                    /*similarDeals.clear();
                    nearbyDeals.clear();
                    similarDeals.addAll(dealMisc.genericDeal.similarDeals);
                    nearbyDeals.addAll(dealMisc.genericDeal.nearbyDeals);

                    activity.onHaveData(dealMisc.genericDeal);*/
                }
                else
                {
                    activity.onNoData();
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
        }
    }

    private String getDealsMisc(String id, String businessId) throws IOException {
        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(ID, id);
        params.put("businessDetail", businessId);
        params.put("type", "deals");
        if(!BizStore.username.isEmpty()) {
            params.put(MSISDN, BizStore.username);
        }

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