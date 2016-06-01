package com.ooredoo.bizstore.asynctasks;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.PromoStatePagerAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.CirclePageIndicator;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
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
public class PromoTask extends BaseAsyncTask<String, Void, String> {
    private PromoStatePagerAdapter adapter;

    private ViewPager viewPager;

    private CirclePageIndicator circlePageIndicator;

    private ProgressBar pbPromo;

    private final static String SERVICE_NAME = "/promotionaldeals?";

    private HomeActivity activity;

    public PromoTask(HomeActivity activity, PromoStatePagerAdapter adapter, ViewPager viewPager,
                     CirclePageIndicator circlePageIndicator, ProgressBar pbPromo) {
        this.adapter = adapter;
        this.activity = activity;
        this.viewPager = viewPager;
        this.circlePageIndicator = circlePageIndicator;
        this.pbPromo = pbPromo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(pbPromo != null)
        {
            pbPromo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getPromos();
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
        if(pbPromo != null)
        {
            pbPromo.setVisibility(View.GONE);
        }


        adapter.clear();

        if(result != null) {
            Gson gson = new Gson();

            try {
                Response response = gson.fromJson(result, Response.class);

                if(response.resultCode != -1) {

                    List<GenericDeal> deals;

                    viewPager.setBackground(null);

                    deals = response.deals;

                    handleIndicatorVisibility(deals.size(), circlePageIndicator);

                    adapter.setData(deals);

                    if(BizStore.getLanguage().equals("ar")) {
                        adapter.notifyDataSetChanged();

                        viewPager.setCurrentItem(deals.size() - 1);
                    }
                } else {
                    viewPager.setBackgroundColor(activity.getResources().getColor(R.color.banner));
                    handleIndicatorVisibility(0, circlePageIndicator);
                }
            } catch(JsonSyntaxException e) {
                e.printStackTrace();
                handleIndicatorVisibility(0, circlePageIndicator);
            }
        } else {
            handleIndicatorVisibility(0, circlePageIndicator);
            Toast.makeText(activity, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
            Logger.print("PromoTask: Failed to download banners due to no internet");
        }

        adapter.notifyDataSetChanged();
    }

    private String getPromos() throws IOException {
        String result;

        /*final String KEY = "PROMO_DEALS";

        final String cachedData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, KEY.concat("_UPDATE"));

        if(hasInternetConnection(activity) && (isNullOrEmpty(cachedData) || updateFromServer)) {

            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            result = getJson(url);

            updateVal(activity, KEY, result);
            updateVal(activity, KEY.concat("_UPDATE"), currentTimeMillis());
        } else {
            result = cachedData;
        }*/

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("Promo Url: "+url.toString());

        result = getJson(url);

        updateVal(activity, KEY, result);
        updateVal(activity, KEY.concat("_UPDATE"), currentTimeMillis());

        Logger.print("getPromos: " + result);

        return result;
    }

    final String KEY = "PROMO_DEALS";

    public String getCache()
    {
        String result = null;

        final String cachedData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, KEY.concat("_UPDATE"));

        if(!isNullOrEmpty(cachedData) && (!hasInternetConnection(activity) || !updateFromServer))
        {
            result = cachedData;
        }

        return result;
    }
}