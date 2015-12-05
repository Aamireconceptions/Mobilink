package com.ooredoo.bizstore.asynctasks;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.BusinessDetail;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.utils.DialogUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Babar
 * @since 26-Jun-15.
 */
public class BusinessMiscTask extends BaseAsyncTask<String, Void, String> {
    private BusinessDetailActivity detailActivity;

    private SnackBarUtils snackBarUtils;

    private ProgressBar progressBar;
    private final static String SERVICE_NAME = "/fullDetails?";

    public BusinessMiscTask(BusinessDetailActivity detailActivity,
                            SnackBarUtils snackBarUtils, ProgressBar progressBar) {
        this.detailActivity = detailActivity;

        this.progressBar = progressBar;

        this.snackBarUtils = snackBarUtils;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

       // dialog = DialogUtils.createCustomLoader(detailActivity, "Loading...");

       // if(progressBar != null) { progressBar.setVisibility(View.VISIBLE); }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getBusinessMisc(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ///closeDialog(dialog);

        if(progressBar != null) { progressBar.setVisibility(View.GONE); }

        if(result != null) {
            Business business;

            Gson gson = new Gson();

            //Logger.print("Business Misc: " + result);
            try {
                BusinessDetail businessDetail = gson.fromJson(result, BusinessDetail.class);

                business = businessDetail.src;

                if(businessDetail.resultCode != -1) {
                    if(business != null) {
                        Logger.print("title:" + business.title);
                        detailActivity.populateMisc(business);
                    }
                }

            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
           // snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String getBusinessMisc(String dealId) throws IOException {
        String result;

        InputStream inputStream = null;

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);
            params.put("type", "business");
            params.put("id", dealId);

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("getBusinessMisc() URL:" + url.toString());

            result = getJson(url);

            Logger.print("getBusinessMisc: " + result);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }

        return result;
    }
}