package com.ooredoo.bizstore.asynctasks;

import android.app.Dialog;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Babar
 * @since 26-Jun-15.
 */
public class DealsTask extends BaseAsyncTask<String, Void, String> {
    private HomeActivity homeActivity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private TextView tvDealsOfTheDay;

    private final static String SERVICE_NAME = "/deals?";

    public static String sortColumn = "createdate"; //Default new deals
    public static String subCategories;

    public String category;

    private Dialog loader;

    private ListView listView;

    public DealsTask(HomeActivity homeActivity, ListViewBaseAdapter adapter, ProgressBar progressBar) {
        this.homeActivity = homeActivity;

        this.adapter = adapter;

        this.progressBar = progressBar;
    }

    public void setTvDealsOfTheDay(TextView tvDealsOfTheDay) {
        this.tvDealsOfTheDay = tvDealsOfTheDay;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        /*if(listView != null) listView.setVisibility(View.GONE);
        if(loaderView != null) loaderView.setVisibility(View.VISIBLE);*/

        loader = DialogUtils.createCustomLoader(homeActivity, "Loading...");

        /*if(progressBar != null) { progressBar.setVisibility(View.VISIBLE); }*/
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            category = params[0];
            return getDeals(category);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        /*if(listView != null) listView.setVisibility(View.VISIBLE);
        if(loaderView != null) loaderView.setVisibility(View.GONE);*/

        if(loader != null && loader.isShowing())
            loader.dismiss();

        /*if(progressBar != null) { progressBar.setVisibility(View.GONE); }*/

        if(result != null) {
            //List<GenericDeal> deals = null;

            Logger.logI("DEALS: " + category, result);

            Gson gson = new Gson();

            try {
                Response response = gson.fromJson(result, Response.class);

                /*if(response.deals != null)
                    deals = response.deals;*/

                adapter.clearData();

                if(response.resultCode != -1)
                {
                    List<GenericDeal> deals = response.deals;

                    for(GenericDeal genericDeal : deals) {
                        Logger.print("title:"+genericDeal.title);
                    }

                    showTvDealsOfTheDay();

                    adapter.setData(deals);
                }

                adapter.notifyDataSetChanged();

            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void showTvDealsOfTheDay() {
        if(tvDealsOfTheDay != null) {
            tvDealsOfTheDay.setVisibility(View.VISIBLE);
        }
    }

    private String getDeals(String category) throws IOException {
        String result;

        InputStream inputStream = null;

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);
            params.put(CATEGORY, category);

            Logger.print("Sort by: " + sortColumn);
            Logger.print("Sub Categories: " + subCategories);

            if(isNotNullOrEmpty(sortColumn)) {
                params.put("sort", sortColumn);
            }

            if(isNotNullOrEmpty(subCategories)) {
                params.put("subcategories", subCategories);
            }

            if(homeActivity.doApplyRating && homeActivity.ratingFilter != null) {
                params.put("rating", homeActivity.ratingFilter);
            }

            if(homeActivity.doApplyDiscount) {
                params.put("min_discount", String.valueOf(homeActivity.minDiscount));
                params.put("max_discount", String.valueOf(homeActivity.maxDiscount));
            }

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("getDeals() URL:" + url.toString());

           /* HttpURLConnection connection = openConnectionAndConnect(url);

            inputStream = connection.getInputStream();

            result = readStream(inputStream);*/

            result = getJson(url);

            Logger.print("getDeals: " + result);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }

        return result;
    }
}