package com.ooredoo.bizstore.asynctasks;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.TopMallsStatePagerAdapter;
import com.ooredoo.bizstore.model.MallResponse;
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
public class TopMallsTask extends BaseAsyncTask<String, Void, String> {

    private TopMallsStatePagerAdapter adapter;

    private ViewPager viewPager;

    private ProgressBar pbTopMalls;

    private final static String SERVICE_NAME = "/topmalls?";

    private HomeActivity activity;

    TextView tvTopMalls; RelativeLayout rlTopMall;

    public TopMallsTask(HomeActivity activity, TopMallsStatePagerAdapter adapter, ViewPager viewPager,
                        ProgressBar pbTopMalls, TextView tvTopMalls, RelativeLayout rlTopMall) {
        this.adapter = adapter;
        this.activity = activity;
        this.viewPager = viewPager;
        this.pbTopMalls = pbTopMalls;
        this.tvTopMalls = tvTopMalls;
        this.rlTopMall = rlTopMall;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(pbTopMalls != null) pbTopMalls.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getTopMalls();
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
         if(pbTopMalls != null)pbTopMalls.setVisibility(View.GONE);

        adapter.clear();

        if(result != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewPager.setBackground(null);
            }
            else
            {
                viewPager.setBackgroundDrawable(null);
            }

            Gson gson = new Gson();

            try
            {
                tvTopMalls.setVisibility(View.VISIBLE);
                rlTopMall.setVisibility(View.VISIBLE);

                MallResponse mallResponse = gson.fromJson(result, MallResponse.class);

                if(mallResponse.resultCode != - 1)
                {
                    adapter.setData(mallResponse.malls);

                    if (BizStore.getLanguage().equals("ar")) {
                        adapter.notifyDataSetChanged();

                        viewPager.setCurrentItem(mallResponse.malls.size() - 1);
                    }

                    updateVal(activity, KEY, result);
                    updateVal(activity, KEY.concat("_UPDATE"), currentTimeMillis());
                }
                else
                {
                    tvTopMalls.setVisibility(View.GONE);
                    rlTopMall.setVisibility(View.GONE);
                }
            }
            catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Logger.print("TopMallsAsyncTask: Failed to download Banners due to no internet");
        }

        adapter.notifyDataSetChanged();
    }

    private String getTopMalls() throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getTopMalls() URL:" + url.toString());

        result = getJson(url);

        Logger.print("getTopMalls:" + result);

        return result;
    }

    final String KEY = "TOP_MALLS";
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