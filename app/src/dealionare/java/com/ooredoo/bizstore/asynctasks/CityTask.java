package com.ooredoo.bizstore.asynctasks;

import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.StringUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Babar on 23-Sep-15.
 */
public class CityTask extends BaseAsyncTask<String, Void, String>
{
    private static final String LOG_TAG = "cityTask";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return saveCity(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Logger.logI(LOG_TAG, StringUtils.isNotNullOrEmpty(result) ? result : "ERR_UPDATING_VIEWS_COUNT");
    }

    private String saveCity(String cities) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();

        params.put("cities", cities);

        setServiceUrl("changesettings", params);



        result = getJson();

        Logger.print("city task result:" + result);

        return result;
    }

}
