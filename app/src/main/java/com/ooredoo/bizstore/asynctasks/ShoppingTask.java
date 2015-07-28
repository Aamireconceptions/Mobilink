package com.ooredoo.bizstore.asynctasks;

import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.System.currentTimeMillis;

/**
 * @author Babar
 * @since 18-Jun-15.
 */
public class ShoppingTask extends BaseAsyncTask<String, Void, String>
{
    private HomeActivity homeActivity;

    private GridViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private OnDealsTaskFinishedListener dealsTaskFinishedListener;

    public static String sortColumn = "createdate";

    public static String subCategories;

    private final static String SERVICE_NAME = "/deals?";

    public ShoppingTask(HomeActivity homeActivity, GridViewBaseAdapter adapter,
                        ProgressBar progressBar, SnackBarUtils snackBarUtils,
                        Fragment fragment)
    {
        this.homeActivity = homeActivity;

        this.adapter = adapter;

        this.progressBar = progressBar;

        this.snackBarUtils = snackBarUtils;

        dealsTaskFinishedListener = (OnDealsTaskFinishedListener) fragment;
    }


    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getDeals(params[0]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        homeActivity.onRefreshCompleted();

        this.progressBar.setVisibility(View.GONE);

        if(result != null)
        {
            Gson gson = new Gson();

            Response response = gson.fromJson(result, Response.class);

            adapter.clearData();

            if(response.resultCode != -1)
            {
                dealsTaskFinishedListener.onHaveDeals();

                List<GenericDeal> deals = new ArrayList<>();

                if(response.deals != null)
                    deals = response.deals;

                adapter.setData(deals);

            }
            else
            {
                dealsTaskFinishedListener.onNoDeals();
            }

            adapter.notifyDataSetChanged();
        }
        else
        {
            snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String getDeals(String category) throws IOException
    {
        String result = null;

        boolean isFilterEnabled = false;

        InputStream inputStream = null;

        try
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);
            params.put(CATEGORY, category);

            Logger.print("Sort by: " + sortColumn);
            Logger.print("Sub Categories: " + subCategories);

            if(isNotNullOrEmpty(sortColumn)) {
                if(sortColumn.equals("views"))
                    isFilterEnabled = true;
                params.put("sort", sortColumn);
            }

            if(isNotNullOrEmpty(subCategories)) {
                isFilterEnabled = true;
                params.put("subcategories", subCategories);
            }

            if(homeActivity.doApplyRating && homeActivity.ratingFilter != null) {
                isFilterEnabled = true;
                params.put("rating", homeActivity.ratingFilter);
            }

            if(homeActivity.doApplyDiscount) {
                isFilterEnabled = true;
                params.put("min_discount", String.valueOf(homeActivity.minDiscount));
                params.put("max_discount", String.valueOf(homeActivity.maxDiscount));
            }

            final String KEY = PREFIX_DEALS.concat(category);
            final String UPDATE_KEY = KEY.concat("_UPDATE");

            if(isFilterEnabled || checkIfUpdateData(homeActivity, UPDATE_KEY)) {

                String query = createQuery(params);

                URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

                Logger.print("Shopping url: " + url.toString());

                result = getJson(url);
                if(!isFilterEnabled) {
                    updateVal(homeActivity, KEY, result);
                    updateVal(homeActivity, UPDATE_KEY, currentTimeMillis());
                }
            } else {
                result = getStringVal(homeActivity, KEY);
            }

            Logger.print("Shopping getDeals: "+result);

            return result;
        }
        finally
        {
            Logger.print("Entered Finally");

            if(inputStream != null)
            {
                Logger.print("Closing Stream");

                inputStream.close();
            }

        }
    }
}