package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.FeaturedStatePagerAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;
import static java.lang.System.currentTimeMillis;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class FeaturedTask extends BaseAsyncTask<String, Void, String>
{
    private FeaturedStatePagerAdapter adapter;

    private ViewPager viewPager;

    private CirclePageIndicator featuredIndicator;

    private final static String SERVICE_NAME = "/featureddeals?";

    private HomeActivity activity;

    public FeaturedTask(HomeActivity activity, FeaturedStatePagerAdapter adapter,
                        ViewPager viewPager, CirclePageIndicator featuredIndicator)
    {
        this.adapter = adapter;
        this.activity = activity;
        this.viewPager = viewPager;
        this.featuredIndicator = featuredIndicator;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getFeatured();
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

        adapter.clear();

        if(result != null)
        {
            viewPager.setBackground(null);

            Gson gson = new Gson();

            try {
                Response response = gson.fromJson(result, Response.class);

                if (response.resultCode != -1)
                {
                    List<GenericDeal> deals;

                    viewPager.setBackground(null);

                    deals = response.deals;

                    handleIndicatorVisibility(deals.size(), featuredIndicator);

                    adapter.setData(deals);

                    if(BizStore.getLanguage().equals("ar"))
                    {
                        adapter.notifyDataSetChanged();

                        viewPager.setCurrentItem(deals.size() - 1);
                    }
                }
                else
                {
                    viewPager.setBackgroundResource(R.drawable.feature_banner);
                }
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Logger.print("FeaturedAsyncTask: Failed to download banners due to no internet");
        }

        adapter.notifyDataSetChanged();
    }

    private String getFeatured() throws IOException
    {
        String result;

        InputStream inputStream = null;

        final String KEY = "FEATURED_DEALS";

        final String cachedData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, KEY.concat("_UPDATE"));

        if(hasInternetConnection(activity) && (isNullOrEmpty(cachedData) || updateFromServer)) {

        try
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("getFeatured() URL:"+ url.toString());

            result = getJson(url);

            updateVal(activity, KEY, result);
            updateVal(activity, KEY.concat("_UPDATE"), currentTimeMillis());

        }
        finally
        {
            if(inputStream != null)
            {
                inputStream.close();
            }
        }
        } else {
            result = cachedData;
        }

        Logger.print("getFeatured: "+result);

        return result;
    }
}