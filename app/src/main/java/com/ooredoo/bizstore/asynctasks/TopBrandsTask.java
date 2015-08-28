package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.TopBrandsStatePagerAdapter;
import com.ooredoo.bizstore.model.BrandResponse;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

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
public class TopBrandsTask extends BaseAsyncTask<String, Void, String> {

    private TopBrandsStatePagerAdapter adapter;

    private ViewPager viewPager;

    private ProgressBar pbTopBrands;

    private final static String SERVICE_NAME = "/topbrand?";

    private HomeActivity activity;

    public TopBrandsTask(HomeActivity activity, TopBrandsStatePagerAdapter adapter, ViewPager viewPager,
                         ProgressBar pbTopBrands) {
        this.adapter = adapter;
        this.activity = activity;
        this.viewPager = viewPager;
        this.pbTopBrands = pbTopBrands;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(pbTopBrands != null)  pbTopBrands.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getTopBrands(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        setData(result);
    }

    public void setData(String result)
    {
        if(pbTopBrands != null)  pbTopBrands.setVisibility(View.GONE);
        adapter.clear();

        if(result != null) {
            viewPager.setBackground(null);

            Gson gson = new Gson();

            BrandResponse brand = gson.fromJson(result, BrandResponse.class);

            if(brand.resultCode != - 1)
            {
                adapter.setData(brand.brands);

                if(BizStore.getLanguage().equals("ar"))
                {
                    adapter.notifyDataSetChanged();

                    viewPager.setCurrentItem(brand.brands.size() - 1);
                }
            }

        } else {
            Logger.print("TopBrandsAsyncTask: Failed to download Banners due to no internet");
        }

        adapter.notifyDataSetChanged();
    }

    private String getTopBrands(String category) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        //params.put(CATEGORY, category);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getTopBrands() URL:" + url.toString());

        result = getJson(url);

        updateVal(activity, KEY, result);
        updateVal(activity, KEY.concat("_UPDATE"), currentTimeMillis());

        Logger.print("getTopBrands:" + result);

        return result;
    }

    final String KEY = "TOP_BRANDS";

    public String getCache() {
        String result = null;

        final String cachedData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, KEY.concat("_UPDATE"));

        if (!isNullOrEmpty(cachedData) && (!hasInternetConnection(activity) || !updateFromServer)) {
            result = cachedData;
        }

        return result;
    }
}