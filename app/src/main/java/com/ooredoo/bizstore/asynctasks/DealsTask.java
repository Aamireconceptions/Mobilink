package com.ooredoo.bizstore.asynctasks;

import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Babar
 * @since 26-Jun-15.
 */
public class DealsTask extends BaseAsyncTask<String, Void, String>
{
    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    public DealsTask(ListViewBaseAdapter adapter, ProgressBar progressBar) {
        this.adapter = adapter;

        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getDeals(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        progressBar.setVisibility(View.GONE);

        if(result != null) {
            List<GenericDeal> deals;

            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            deals = response.deals;

            for(GenericDeal genericDeal : deals) {
                Logger.print("title:"+genericDeal.title);
            }

            adapter.setData(deals);
            adapter.notifyDataSetChanged();
        }
    }

    private String getDeals(String category) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put("category", category);
        setServiceUrl("deals", params);

        setServiceUrl(BASE_URL + "en/deals/26"); //TODO remove this line

        result = getJson();

        Logger.print("getDeals:"+result);

        return result;
    }

}
