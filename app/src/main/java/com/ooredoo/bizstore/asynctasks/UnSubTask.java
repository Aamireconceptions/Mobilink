package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.widget.Toast;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.GcmPreferences;
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

    private Dialog dialog;

    public UnSubTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = DialogUtils.createCustomLoader(activity, activity.getString(R.string.unsubscribing));
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

        closeDialog(dialog);

        if(result != null) {
            updateVal(activity, LOGIN_STATUS, false);

            activity.finish();

            activity.startActivity(new Intent(activity, MainActivity.class));

            Toast.makeText(activity, R.string.un_sub_success, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(activity, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
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

        setServiceUrl("signout", params);

        result = getJson();

        Logger.print("UnSubscribe: " + result);

        return result;
    }

}
