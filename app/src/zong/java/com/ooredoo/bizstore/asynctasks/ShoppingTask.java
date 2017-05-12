package com.ooredoo.bizstore.asynctasks;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.BrandResponse;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.ShoppingFragment;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NetworkUtils;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;
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

    private ShoppingFragment shoppingFragment;

    public static String sortColumn = "createdate";

    public static String subCategories;

    private final static String SERVICE_NAME = "/listing?";

    public ShoppingTask(HomeActivity homeActivity, GridViewBaseAdapter adapter,
                        ProgressBar progressBar, SnackBarUtils snackBarUtils,
                        Fragment fragment)
    {
        this.homeActivity = homeActivity;

        this.adapter = adapter;

        this.progressBar = progressBar;

        this.snackBarUtils = snackBarUtils;

        dealsTaskFinishedListener = (OnDealsTaskFinishedListener) fragment;

        this.shoppingFragment = (ShoppingFragment) fragment;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        if(progressBar != null) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
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
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        setData(result);
    }

    public void setData(String result)
    {
        dealsTaskFinishedListener.onRefreshCompleted();

        if(progressBar != null) {
            this.progressBar.setVisibility(View.GONE);
        }

        //adapter.clearData();

        subCategories = "";

        if(result != null)
        {
            if(sortColumn.equals("createdate"))
            {
                Gson gson = new Gson();

                try
                {
                    Response response = gson.fromJson(result, Response.class);

                    if(response.resultCode != -1 && response.deals != null && response.deals.size() > 0)
                    {
                        dealsTaskFinishedListener.onHaveDeals();

                        List<GenericDeal> deals = new ArrayList<>();

                        if(response.deals != null) {
                            deals = response.deals;
                        }

                        adapter.setData(deals);

                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        dealsTaskFinishedListener.onNoDeals(R.string.error_no_data);

                       // Toast.makeText(homeActivity, R.string.error_no_data, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JsonSyntaxException e)
                {
                    dealsTaskFinishedListener.onNoDeals(R.string.error_server_down);

                    //Toast.makeText(homeActivity, R.string.error_server_down, Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }
        }
        else
        {
            adapter.clearData();
            adapter.notifyDataSetChanged();

            dealsTaskFinishedListener.onNoDeals(R.string.error_no_internet);
        }

    }

    private String getDeals(String category) throws IOException
    {
        String result;

        boolean isFilterEnabled = false;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(CATEGORY, category);

        String sortColumns = "";

        Logger.print("Sort by: " + sortColumn);
        Logger.print("Sub Categories: " + subCategories);

        if(isNotNullOrEmpty(sortColumn)) {
            if(sortColumn.equals("views"))
                isFilterEnabled = true;
            sortColumns = sortColumn;

            if(sortColumn.equals("createdate"))
            {
                params.put("type", "deals");
            }
            else
            {
                params.put("type", "business");
            }
        }

        if(isNotNullOrEmpty(sortColumns)) {
            if(sortColumns.equals("views"))
                isFilterEnabled = true;

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
            if(isNotNullOrEmpty(sortColumns)) {
                sortColumns = "discount_dsc,".concat(sortColumns);
            } else {
                sortColumns = "discount_dsc";
            }
            isFilterEnabled = true;
        }

        if(homeActivity.doApplyDistance) {
            if(isNotNullOrEmpty(sortColumns)) {
                sortColumns = "distance,".concat(sortColumns);
            } else {
                sortColumns = "distance";
            }
            isFilterEnabled = true;
        }

        if(isNotNullOrEmpty(sortColumns)) {
            params.put("sort", sortColumns);
        }

        if(isNotNullOrEmpty(subCategories)) {
            isFilterEnabled = true;
            params.put("subcategories", subCategories);
        }

        final String KEY = PREFIX_DEALS.concat(category);
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("Shopping url: " + url.toString());

        result = getJson(url);

        if(!isFilterEnabled && !result.contains("No item Found"))
        {
            updateVal(homeActivity, KEY, result);
            updateVal(homeActivity, UPDATE_KEY, currentTimeMillis());
        }

        Logger.print("Shopping getDeals: " + result);

        return result;
    }

    public String getCache(String category)
    {
        String result = null;

        final String KEY = PREFIX_DEALS.concat(category);
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        boolean isFilterEnabled = false;

        if(isNotNullOrEmpty(sortColumn)) {
            if(sortColumn.equals("views"))
                isFilterEnabled = true;
        }

        if(isNotNullOrEmpty(subCategories)) {
            isFilterEnabled = true;
        }

        if(homeActivity.doApplyRating && homeActivity.ratingFilter != null) {
            isFilterEnabled = true;
        }

        if(homeActivity.doApplyDiscount) {
            isFilterEnabled = true;
        }

        if(homeActivity.doApplyDistance) {
            isFilterEnabled = true;
        }

        String cacheData = getStringVal(homeActivity, KEY);

        boolean updateFromServer = checkIfUpdateData(homeActivity, UPDATE_KEY);

        if(!isNullOrEmpty(cacheData) && !isFilterEnabled && (!NetworkUtils.hasInternetConnection(homeActivity) || !updateFromServer))
        {
            result = cacheData;
        }

        return result;
    }
}