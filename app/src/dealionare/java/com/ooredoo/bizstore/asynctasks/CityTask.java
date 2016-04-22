package com.ooredoo.bizstore.asynctasks;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.CitySelectionActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Babar on 23-Sep-15.
 */
public class CityTask extends BaseAsyncTask<String, Void, String>
{
    private static final String LOG_TAG = "cityTask";

    Activity activity;

    Dialog dialog;

    public CityTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader(activity, activity.getString(R.string.please_wait));
        dialog.show();
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

        dialog.dismiss();

        if(result != null)
        {
            try {
                JSONObject jsonObject = new JSONObject(result);

                int code = jsonObject.getInt("result");

                if(code == 0)
                {
                    activity.setResult(activity.RESULT_OK);

                    activity.finish();
                }
                else
                {
                    Toast.makeText(activity, "Some thing went wrong, Please try again!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(activity, R.string.error_server_down, Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(activity, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }


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
