package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.adapters.ViewedRatedAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.DODResponse;
import com.ooredoo.bizstore.ui.fragments.HomeFragment;
import com.ooredoo.bizstore.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;

/**
 * Created by Babar on 13-Jan-16.
 */
public class ViewedRatedTask extends AsyncTask<String, Void, String>
{
    private Context context;

    private ViewedRatedAdapter adapter;

    private String category;

    private HomeFragment homeFragment;

    private OnDealsTaskFinishedListener dealsTaskFinishedListener;

    private static final String SERVICE_NAME  = "/viewednrated?";

    public ViewedRatedTask(Context context, ViewedRatedAdapter adapter, Fragment fragment)
    {
        this.context = context;

        this.adapter = adapter;

        this.homeFragment = (HomeFragment) fragment;

        dealsTaskFinishedListener = (OnDealsTaskFinishedListener) fragment;
    }

    @Override
    protected String doInBackground(String... params)
    {
            try {
                category = params[0];

                return getViewedRated();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        setData(s);
    }

    public void setData(String result)
    {
        Logger.print("ViewedRated result: "+result);
        dealsTaskFinishedListener.onRefreshCompleted();
        adapter.clear();

        if(result != null) {
            Gson gson = new Gson();

            try {

                DODResponse response = gson.fromJson(result, DODResponse.class);

                if (response.resultCode != -1) {
                    if(response.dods != null  && response.dods.size() > 0)
                    {
                        dealsTaskFinishedListener.onHaveDeals();

                       /* final String KEY = PREFIX_DEALS.concat(category);
                        final String UPDATE_KEY = KEY.concat("_UPDATE");

                        updateVal((Activity) context, KEY, result);
                        updateVal((Activity) context, UPDATE_KEY, currentTimeMillis());*/
                    }

                   // adapter.setData(response.dods);

                    homeFragment.addMostViewedAndTopRated(response.dods);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
            dealsTaskFinishedListener.onNoDeals(0);
        }

        adapter.notifyDataSetChanged();
    }

    private String getViewedRated() throws IOException {
        HashMap<String, String> params = new HashMap<>();
        params.put(BaseAsyncTask.OS, BaseAsyncTask.ANDROID);

        String query = createQuery(params);

        URL url = new URL(BaseAsyncTask.BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("ViewednRated URL: "+url);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(BaseAsyncTask.HTTP_X_USERNAME, BizStore.username);
        connection.setRequestProperty(BaseAsyncTask.HTTP_X_PASSWORD, BizStore.password);
        connection.setConnectTimeout(BaseAsyncTask.CONNECTION_TIME_OUT);
        connection.setReadTimeout(BaseAsyncTask.READ_TIME_OUT);
        connection.setRequestMethod(BaseAsyncTask.METHOD);
        connection.setDoInput(true);
        connection.connect();

        InputStream is = connection.getInputStream();

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new InputStreamReader(is, BaseAsyncTask.ENCODING));

            StringBuilder stringBuilder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        }
        finally {
            if(reader != null)
            {
                reader.close();
            }
        }
    }

    private String createQuery(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder stringBuilder = new StringBuilder();

        boolean isFirst = true;

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if(isFirst)
            {
                isFirst = false;
            }
            else
            {
                stringBuilder.append("&");
            }

            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(entry.getValue(), BaseAsyncTask.ENCODING));
        }

        return stringBuilder.toString();
    }

    public String getCache(String category)
    {
        String result = null;

        final String KEY = PREFIX_DEALS.concat(category);
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        Activity activity = (Activity) context;

        String cacheData = getStringVal(activity, KEY);

        boolean updateFromServer = checkIfUpdateData(activity, UPDATE_KEY);

        if(!isNullOrEmpty(cacheData) && (!hasInternetConnection(activity) || !updateFromServer))
        {
            result = cacheData;
        }

        return result;
    }
}