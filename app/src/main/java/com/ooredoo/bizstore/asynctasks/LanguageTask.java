package com.ooredoo.bizstore.asynctasks;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Babar on 27-Aug-15.
 */
public class LanguageTask extends BaseAsyncTask<String, Void, String>
{
    private final static String SERVICE_NAME = "/promotionaldeals?";

    @Override
    protected String doInBackground(String... params)
    {
        try {
            return setLanguage(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String setLanguage(String lang) throws IOException
    {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put("lang", lang);

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        result = getJson(url);

        Logger.print("setLanguage: " + result);

        return result;
    }
}
