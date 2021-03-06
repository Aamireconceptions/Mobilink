package com.ooredoo.bizstore.asynctasks;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.DealDetail;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
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
public class DealDetailTask extends BaseAsyncTask<String, Void, String> {
    private DealDetailActivity detailActivity;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private final static String SERVICE_NAME = "/getdetails?";

    public DealDetailTask(DealDetailActivity detailActivity, ProgressBar progressBar, SnackBarUtils snackBarUtils) {
        this.detailActivity = detailActivity;

        this.progressBar = progressBar;

        this.snackBarUtils = snackBarUtils;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = DialogUtils.createCustomLoader(detailActivity, detailActivity.getString(R.string.please_wait));
        if(progressBar != null) { progressBar.setVisibility(View.VISIBLE); }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getDealDetails(params[0]);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        closeDialog(dialog);

        if(progressBar != null) { progressBar.setVisibility(View.GONE); }

        if(result != null) {
            GenericDeal deal;

            Gson gson = new Gson();

            Logger.print("DETAIL: " + result);
            try {
                DealDetail dealDetail = gson.fromJson(result, DealDetail.class);

                deal = dealDetail.deal;

                if(dealDetail.resultCode != -1) {
                    if(dealDetail != null) {
                        Logger.print("title:" + deal.title);
                        detailActivity.populateData(deal);
                    }
                }

            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(detailActivity, R.string.error_no_internet, Toast.LENGTH_LONG).show();
            detailActivity.finish();
            //snackBarUtils.showSimple(R.string.error_no_internet, Snackbar.LENGTH_SHORT);
        }
    }

    private String getDealDetails(String dealId) throws IOException {
        String result;

        InputStream inputStream = null;

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(OS, ANDROID);
            params.put("type", "deals");
            params.put("id", dealId);

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("getDetails() URL:" + url.toString());

            result = getJson(url);

            Logger.print("getDetails: " + result);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }

        return result;
    }
}