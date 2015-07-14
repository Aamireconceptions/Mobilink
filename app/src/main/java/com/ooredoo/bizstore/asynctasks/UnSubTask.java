package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;

import com.ooredoo.bizstore.utils.Logger;

import java.io.IOException;
import java.util.HashMap;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Pehlaj Rai
 * @since 14-Jul-15
 */
public class UnSubTask extends BaseAsyncTask<String, Void, String> {

    private Activity activity;

    public UnSubTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String msisdn = params[0];
        try {
            return unSubscribe(msisdn);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null) {
            updateVal(activity, LOGIN_STATUS, false);
            activity.finish();
        }
    }

    /**
     * Un-Subscribe user, invalidate user session etc & close app
     *
     * @param msisdn
     * @throws IOException
     */
    private String unSubscribe(String msisdn) throws IOException {
        String result;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put("msisdn", msisdn);

        setServiceUrl("unsubscribe", params);

        result = getJson();

        Logger.print("UnSubscribe: " + result);

        return result;
    }

}
