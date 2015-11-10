package com.ooredoo.bizstore.asynctasks;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.model.Response;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.BitmapProcessor;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.NetworkUtils.hasInternetConnection;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.checkIfUpdateData;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.getStringVal;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;
import static java.lang.System.currentTimeMillis;

/**
 * @author Babar
 * @since 26-Jun-15.
 */
public class NearbyTask extends BaseAsyncTask<String, Void, String>
{
    private HomeActivity homeActivity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    BitmapProcessor bitmapProcessor;

    MemoryCache memoryCache = MemoryCache.getInstance();

    DiskCache diskCache = DiskCache.getInstance();

    private ImageView ivBanner;

    private final static String SERVICE_NAME = "/deals?";

    public static String sortColumn = "createdate"; //Default new deals
    public static String subCategories;

    public String category;

    private OnDealsTaskFinishedListener dealsTaskFinishedListener;
   // private MemoryCache memoryCache;

    private int reqWidth, reqHeight;

    private GoogleMap map;
    private HashMap<String, Marker> genericDealHashMap;
    Resources res;

    public NearbyTask(HomeActivity homeActivity, ListViewBaseAdapter adapter,
                      ProgressBar progressBar, ImageView ivBanner, Fragment fragment,
                      GoogleMap map,  HashMap<String, Marker> genericDealHashMap)
    {
        this.homeActivity = homeActivity;

        this.adapter = adapter;

        this.progressBar = progressBar;

        this.ivBanner = ivBanner;

        dealsTaskFinishedListener = (OnDealsTaskFinishedListener) fragment;

        res = homeActivity.getResources();

        DisplayMetrics displayMetrics = res.getDisplayMetrics();

        reqWidth = displayMetrics.widthPixels;

        reqHeight = (int) Converter.convertDpToPixels(res.getDimension(R.dimen._160sdp)
                / displayMetrics.density);

        this.map = map;

        bitmapProcessor = new BitmapProcessor();

        this.genericDealHashMap = genericDealHashMap;
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

        setData(result);
    }

    public void setData(String result)
    {
        if(progressBar != null) { progressBar.setVisibility(View.GONE); }

        dealsTaskFinishedListener.onRefreshCompleted();

        subCategories = "";
        sortColumn = "createdate";

        adapter.clearData();

        if(result != null)
        {
            Logger.logI("DEALS: " + category, result);

            Gson gson = new Gson();

            try {
                Response response = gson.fromJson(result, Response.class);

                if(response.resultCode != -1)
                {
                    dealsTaskFinishedListener.onHaveDeals();

                    List<GenericDeal> deals = response.deals;

                    if(deals != null)
                    {
                        adapter.setData(deals);

                        //populateMap(deals);

                        String bannerUrl = response.topBannerUrl;

                        if(bannerUrl != null) {
                            String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + bannerUrl;

                            Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                            if(bitmap != null) {
                                ivBanner.setImageBitmap(bitmap);
                            } else {
                                BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(ivBanner, null);
                                bitmapDownloadTask.execute(imgUrl, String.valueOf(reqWidth), String.valueOf(reqHeight));
                            }
                        }
                    } else {
                        dealsTaskFinishedListener.onNoDeals(R.string.error_no_data);
                    }

                }
                else
                {
                    dealsTaskFinishedListener.onNoDeals(R.string.error_no_data);
                }

                }
                catch(JsonSyntaxException e)
                {
                    e.printStackTrace();

                    dealsTaskFinishedListener.onNoDeals(R.string.error_server_down);
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

        if(BizStore.forceStopTasks)
        {
            Logger.print("Force stopped deals task");

            return null;
        }

        boolean isFilterEnabled = false;

        HashMap<String, String> params = new HashMap<>();
        params.put(OS, ANDROID);
        params.put(CATEGORY, category);
        params.put("lat", String.valueOf(HomeActivity.lat));
        params.put("lng", String.valueOf(HomeActivity.lng));


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
            if(isNotNullOrEmpty(sortColumns)) {
                sortColumns = "discount_dsc,".concat(sortColumns);
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

        String query = createQuery(params);

        URL url = new URL(BASE_URL + BizStore.getLanguage() + SERVICE_NAME + query);

        Logger.print("getDeals() URL:" + url.toString());

        result = getJson(url);

        Logger.logI("DEALS_FILTER->" + isFilterEnabled, category);

        if(!isFilterEnabled)
        {
            updateVal(homeActivity, KEY, result);
            updateVal(homeActivity, UPDATE_KEY, currentTimeMillis());
        }

        Logger.print("getDeals: " + result);

        return result;
    }

    public String getCache(String category)
    {
        String result = null;

        final String KEY = PREFIX_DEALS.concat(category);
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        boolean isFilterEnabled = false;

        if(isNotNullOrEmpty(sortColumn)) {
            if(sortColumn.equals("views"))
                isFilterEnabled = true;
        }

        if(isNotNullOrEmpty(subCategories)) {
            isFilterEnabled = true;
        }

        if(homeActivity.doApplyRating && homeActivity.ratingFilter != null) {
            isFilterEnabled = true;
        }

        if(homeActivity.doApplyDiscount) {

            isFilterEnabled = true;
        }

        String cacheData = getStringVal(homeActivity, KEY);

        boolean updateFromServer = checkIfUpdateData(homeActivity, UPDATE_KEY);

        if(!isNullOrEmpty(cacheData) && !isFilterEnabled && (!hasInternetConnection(homeActivity) || !updateFromServer))
        {
            result = cacheData;
        }

        return result;
    }
}