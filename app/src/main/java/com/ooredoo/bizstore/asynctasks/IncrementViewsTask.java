package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;

import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.HashMap;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Pehlaj Rai
 * @since 03-Jul-15
 */
public class IncrementViewsTask extends BaseAsyncTask<Void, Void, String> {

    private static final String LOG_TAG = "IncrementViewsTask";

    private Activity mActivity;

    private String type;
    private long typeId;

    public IncrementViewsTask(Activity activity, String type, long typeId) {
        this.type = type;
        this.typeId = typeId;
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return incrementViews();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Logger.logI(LOG_TAG, isNotNullOrEmpty(result) ? result : "ERR_UPDATING_VIEWS_COUNT");
    }

    private String incrementViews() throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();

        params.put("type", type);
        params.put("id", String.valueOf(typeId));

        setServiceUrl("incrementviews", params);

        result = getJson();

        Logger.print("incrementViews:" + result);

        return result;
    }

}
