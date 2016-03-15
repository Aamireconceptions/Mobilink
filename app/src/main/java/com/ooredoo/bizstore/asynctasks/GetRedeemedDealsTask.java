package com.ooredoo.bizstore.asynctasks;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.RedeemedDealsBaseAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Babar on 07-Aug-15.
 */
public class GetRedeemedDealsTask extends BaseAsyncTask<String, Void, String>
{
    private Context context;

    private RedeemedDealsBaseAdapter adapter;

    private ProgressBar progressBar;

    private List<GenericDeal> deals;

    private OnDealsTaskFinishedListener onDealsTaskFinishedListener;

    private final static String SERVICE_NAME = "/redeemedvouchers?";


    public GetRedeemedDealsTask(Context context, RedeemedDealsBaseAdapter adapter,
                                ProgressBar progressBar, List<GenericDeal> deals)
    {
        this.context = context;

        this.adapter = adapter;

        this.progressBar = progressBar;

        this.deals = deals;

        onDealsTaskFinishedListener = (OnDealsTaskFinishedListener) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(progressBar != null) { progressBar.setVisibility(View.VISIBLE); }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getRedeemedDeals(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        onDealsTaskFinishedListener.onRefreshCompleted();

        if(progressBar != null) { progressBar.setVisibility(View.GONE); }

        deals.clear();

        if(result != null) {

            Gson gson = new Gson();

            try {
                Response response = gson.fromJson(result, Response.class);

                if(response.resultCode != -1)
                {
                    onDealsTaskFinishedListener.onHaveDeals();

                   // deals = response.deals;

                    deals.addAll(response.deals);
                }
                else
                {
                    onDealsTaskFinishedListener.onNoDeals(R.string.error_no_deal_redeemed);
                }

            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
            onDealsTaskFinishedListener.onNoDeals(R.string.error_no_internet);
        }

        adapter.notifyDataSetChanged();
    }

    private String getRedeemedDeals(String type) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        //params.put(TYPE, type);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getRedeemDeals URL: "+url);
        result = getJson(url);

        Logger.print("getRedeemedDeals: " + result);

        return result;
    }
}
