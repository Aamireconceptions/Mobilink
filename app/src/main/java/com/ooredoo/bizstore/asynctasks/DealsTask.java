package com.ooredoo.bizstore.asynctasks;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.System.currentTimeMillis;

/**
 * @author Babar
 * @since 26-Jun-15.
 */
public class DealsTask extends BaseAsyncTask<String, Void, String> {
    private HomeActivity homeActivity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private final static String SERVICE_NAME = "/deals?";

    public static String sortColumn = "createdate"; //Default new deals
    public static String subCategories;

    public String category;

    private OnDealsTaskFinishedListener dealsTaskFinishedListener;
    private MemoryCache memoryCache;

    private int reqWidth, reqHeight;

    public DealsTask(HomeActivity homeActivity, ListViewBaseAdapter adapter,
                     ProgressBar progressBar, ImageView ivBanner, Fragment fragment)
    {
        this.homeActivity = homeActivity;

        this.adapter = adapter;

        this.progressBar = progressBar;

        this.ivBanner = ivBanner;

        dealsTaskFinishedListener = (OnDealsTaskFinishedListener) fragment;

        memoryCache = MemoryCache.getInstance();

        Resources res = homeActivity.getResources();

        DisplayMetrics displayMetrics = res.getDisplayMetrics();

        reqWidth = displayMetrics.widthPixels;

        reqHeight = (int) Converter.convertDpToPixels(res.getDimension(R.dimen._160sdp)
                / displayMetrics.density);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

       // homeActivity.showLoader();

        if(progressBar != null) { progressBar.setVisibility(View.VISIBLE); }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            category = params[0];
            return getDeals(category);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

       // homeActivity.hideLoader();

        subCategories = "";
        sortColumn = "createdate";

        dealsTaskFinishedListener.onRefreshCompleted();

        adapter.clearData();

        if(progressBar != null) { progressBar.setVisibility(View.GONE); }

        if(result != null) {
            Logger.logI("DEALS: " + category, result);

            Gson gson = new Gson();

            try {
                Response response = gson.fromJson(result, Response.class);

                if(response.resultCode != -1)
                {
                    dealsTaskFinishedListener.onHaveDeals();

                    List<GenericDeal> deals = response.deals;

                    adapter.setData(deals);

                    String bannerUrl = response.topBannerUrl;

                    if(bannerUrl != null)
                    {
                        String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + bannerUrl;

                        Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                        if(bitmap != null)
                        {
                            ivBanner.setImageBitmap(bitmap);
                        }
                        else
                        {
                            BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(ivBanner,
                                    null);
                            bitmapDownloadTask.execute(imgUrl, String.valueOf(reqWidth),
                                    String.valueOf(reqHeight));
                        }
                    }
                }
                else
                {
                    dealsTaskFinishedListener.onNoDeals(R.string.error_no_data);
                }

            } catch(JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        else
        {
            dealsTaskFinishedListener.onNoDeals(R.string.error_no_internet);
        }

        adapter.notifyDataSetChanged();
    }

    private String getDeals(String category) throws IOException {
        String result;

        boolean isFilterEnabled = false;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(CATEGORY, category);

        Logger.print("Sort by: " + sortColumn);
        Logger.print("Sub Categories: " + subCategories);

        String sortColumns = "";
        if(isNotNullOrEmpty(sortColumn)) {
            if(sortColumn.equals("views"))
                isFilterEnabled = true;
            sortColumns = sortColumn;
        }

        if(isNotNullOrEmpty(subCategories)) {
            isFilterEnabled = true;
            params.put("subcategories", subCategories);
        }

        if(homeActivity.doApplyRating && homeActivity.ratingFilter != null) {
            isFilterEnabled = true;
            params.put("rating", homeActivity.ratingFilter);
        }

        if(homeActivity.doApplyDiscount) {
            //TODO sort by discount (ASC | DESC)
            if(isNotNullOrEmpty(sortColumns)) {
                sortColumns = sortColumns.concat(",discount_dsc");
            } else {
                sortColumns = "discount_dsc";
            }
            isFilterEnabled = true;
        }

        if(isNotNullOrEmpty(sortColumns)) {
            params.put("sort", sortColumns);
        }

        Logger.logI("SORT BY", "Columns->" + sortColumns);

        final String KEY = PREFIX_DEALS.concat(category);
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        if(isFilterEnabled || checkIfUpdateData(homeActivity, UPDATE_KEY)) {

            String query = createQuery(params);

            URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

            Logger.print("getDeals() URL:" + url.toString());

            result = getJson(url);

            Logger.logI("DEALS_FILTER->" + isFilterEnabled, category);

            if(!isFilterEnabled) {
                updateVal(homeActivity, KEY, result);
                updateVal(homeActivity, UPDATE_KEY, currentTimeMillis());
            }
        } else {
            result = getStringVal(homeActivity, KEY);
        }

        Logger.print("getDeals: " + result);

        return result;
    }
}