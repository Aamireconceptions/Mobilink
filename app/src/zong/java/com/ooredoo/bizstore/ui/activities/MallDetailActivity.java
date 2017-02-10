package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.MallDetailAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapForceDownloadTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.asynctasks.LocationsTask;
import com.ooredoo.bizstore.asynctasks.MallDetailTask;
import com.ooredoo.bizstore.asynctasks.MallsMiscTask;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.MallDeals;
import com.ooredoo.bizstore.model.MallMiscResponse;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.ACTION_BUSINESS_DETAIL;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static com.ooredoo.bizstore.AppConstant.DIALER_PREFIX;
import static com.ooredoo.bizstore.utils.DialogUtils.showRatingDialog;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.String.valueOf;

/**
 * Created by Babar on 19-Jan-16.
 */
public class MallDetailActivity extends BaseActivity implements View.OnClickListener
{
    Bitmap bitmap;
    public String category;

    private ActionBar mActionBar;

    ExpandableListView expandableListView;

    private int id = -1;

    public Business src;

    MemoryCache memoryCache = MemoryCache.getInstance();

    DiskCache diskCache = DiskCache.getInstance();

    private Dialog ratingDialog;

    static String packageName;

    ScrollViewListener scrollViewListener;

    public MallDetailActivity() {
        super();
        layoutResId = R.layout.mall_detail_activity;
    }

    @Override
    public void init() {

        String username = SharedPrefUtils.getStringVal(this, "username");
        String password = SharedPrefUtils.getStringVal(this, "password");
        String secret = SharedPrefUtils.getStringVal(this, "secret");

        if(!username.equals(SharedPrefUtils.EMPTY)) {
            BizStore.username = username;
        }

        if(!password.equals(SharedPrefUtils.EMPTY)) {
            BizStore.password = password;
        }

        BizStore.secret = secret;

        diskCache.requestInit(this);

        setupToolbar();

        handleIntentFilter();

        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.logI("MAll_DETAIL", "onNewIntent");

        setIntent(intent);

        handleIntentFilter();
    }

    private void handleIntentFilter() {
        Intent intent = getIntent();

        if(intent != null) {
            Uri uri = intent.getData();

            if(uri != null) {
                String paramId = uri.getQueryParameter("id");

                Logger.print("Extras: " + paramId);

                getIntent().putExtra(AppConstant.ID, Integer.parseInt(paramId));
            }

            id = intent.getIntExtra(AppConstant.ID, 0);
            getIntent().putExtra(CATEGORY, DEAL_CATEGORIES[0]);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        Logger.print("OnCreateContextMenu");

        if(mBusiness.locations != null && mBusiness.locations.size() > 0) {
            for (int i = 0; i <= mBusiness.locations.size() - 1; i++) {
                if (mBusiness.locations.size() > 1 ||
                        (mBusiness.location != null)
                                &&
                                (!mBusiness.location.equalsIgnoreCase(mBusiness.locations.get(i).title))) {
                    menu.add(1, mBusiness.locations.get(i).id, 0, mBusiness.locations.get(i).title);
                }
            }
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int id = item.getItemId();

        Logger.print("menuId: "+id);

        LocationsTask locationsTask = new LocationsTask(this);
        locationsTask.execute(String.valueOf(id), "deals", item.getTitle().toString());

        return super.onContextItemSelected(item);
    }

    View header;
    SnackBarUtils snackBarUtils;
    TextView tvLocations;
    LinearLayout llSimilarNearby;
    private void initViews() {
        Logger.print("businessId business, "+ id);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        header = layoutInflater.inflate(R.layout.fragment_mall_detail, null);

        scrollViewListener = new ScrollViewListener(mActionBar);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
              //  scrollViewListener.onScrollChanged(header);
            }
        });
        expandableListView.addHeaderView(header);

        packageName = getPackageName();

        src = (Business) intent.getSerializableExtra("business");

        if(src != null) {

            id = src.id;

            Logger.logI("DETAIL_ID", valueOf(id));
        }

        category = intent.getStringExtra(CATEGORY);

        snackBarUtils = new SnackBarUtils(this, findViewById(R.id.root));

        if(src == null)
        {
            MallDetailTask detailTask = new MallDetailTask(this, null, snackBarUtils);
            detailTask.execute(String.valueOf(id));
        }
        else
        {
            populateData(src);
        }
    }
    ImageView ivDetail;

    ProgressBar progressBar;

    List<String> groupList;

    List<List<?>> childList;

    //RelativeLayout rlDescription;

    TextView tvDescription, tvDescriptionArrow, tvMenuArrow;

    LinearLayout llMenu;

    ViewPager galleryPager;

    MallDetailAdapter adapter;

    Business mBusiness;
    PopupMenu popupMenu;
    LinearLayout llDirections;
    RelativeLayout rlHeader;
    TextView tvCity, tvCity2;
    int color;

    Button btBrands, btDeals;

    public void populateData(final Business business) {
        if(business != null) {
            scrollViewListener.setTitle(business.title);
            color = getIntent().getIntExtra("color", -1);

            id = business.id;
            mBusiness = business;

          /*  if(mBusiness.description == null || mBusiness.description.isEmpty())
            {
                rlDescription.setVisibility(View.GONE);
            }*/

            llDirections = (LinearLayout) findViewById(R.id.directions_layout);
            llDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDirections();
                }
            });

            ImageView ivBrandLogo = (ImageView) findViewById(R.id.brand_logo);

            String brandLogo = business.businessLogo;

            if(brandLogo == null || brandLogo.isEmpty())
            {
                if(business.image != null)
                {
                    brandLogo = business.image.logoUrl;
                }
            }

            Logger.print("BrandLogo: "+brandLogo);

            if(brandLogo != null && !brandLogo.equals(""))
            {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + brandLogo;

                Logger.print("Brand URL: "+imgUrl);
                bitmap = memoryCache.getBitmapFromCache(imgUrl);
Logger.print("Brand Bitmap: "+bitmap);
                if(bitmap != null)
                {
                    ivBrandLogo.setImageBitmap(bitmap);
                }
                else
                {
                    fallBackToDiskCache(imgUrl, ivBrandLogo);
                }
            }
            else
            {
                TextView tvBrandTxt = (TextView) findViewById(R.id.brand_txt);
                FontUtils.setFontWithStyle(this, tvBrandTxt, Typeface.BOLD);

                if(business.color == 0)
                {
                    business.color = color;
                }
                tvBrandTxt.setBackgroundColor(business.color);
                tvBrandTxt.setText(String.valueOf(business.title.charAt(0)));
            }

            groupList = new ArrayList<>();

            childList = new ArrayList<>();

            adapter = new MallDetailAdapter(this, groupList, childList);

            expandableListView.setAdapter(adapter);

            src = business;
            src.id = business.id;

            //src.isFavorite = Favorite.isFavorite(src.id);

            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, "business", id);
            incrementViewsTask.execute();
            if(isNotNullOrEmpty(business.title)) {
               // mActionBar.setTitle(business.title);
            }

            TextView tvTitle = ((TextView) header.findViewById(R.id.tv_title));
            tvTitle.setText(business.title);

            FontUtils.setFontWithStyle(this, tvTitle, Typeface.BOLD);


            tvCity2 = (TextView) header.findViewById(R.id.tv_city);
            tvCity2.setText(business.location);

            ImageView ivShare = (ImageView) findViewById(R.id.iv_share);
            ivShare.setOnClickListener(this);


                    ((RatingBar) header.findViewById(R.id.rating_bar)).setRating(business.rating);
            header.findViewById(R.id.iv_views).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MallDetailActivity.this,
                            getString(R.string.business_has_been_viewed) + " " + business.views + " "
                                    + getString(R.string.times), Toast.LENGTH_SHORT).show();
                }
            });

            TextView tvView = ((TextView) header.findViewById(R.id.tv_views));
            tvView.setText(valueOf(business.views));
            tvView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MallDetailActivity.this,
                            getString(R.string.business_has_been_viewed) + " " + business.views + " "
                                    + getString(R.string.times), Toast.LENGTH_SHORT).show();
                }
            });

            rlHeader = (RelativeLayout) header.findViewById(R.id.rl_header);

            ivDetail = (ImageView) header.findViewById(R.id.detail_img);

            progressBar = (ProgressBar) header.findViewById(R.id.progressBar);

            String detailImageUrl = null;

            if(business.image != null)
            {
                detailImageUrl = business.image.detailBannerUrl;
            }

            Logger.print("detailImgUrl: "+detailImageUrl);

            if(detailImageUrl != null && !detailImageUrl.equals(""))
            {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + detailImageUrl;

                bitmap = memoryCache.getBitmapFromCache(imgUrl);

                if(bitmap != null)
                {
                    ivDetail.setImageBitmap(bitmap);
                }
                else
                {
                    fallBackToDiskCache(imgUrl, ivDetail);
                }
            }
        } else {
            makeText(getApplicationContext(), "No detail found", LENGTH_LONG).show();
        }


        ProgressBar progressBar = (ProgressBar) findViewById(R.id.more_progress);

        MallsMiscTask mallsMiscTask = new MallsMiscTask(this, snackBarUtils, progressBar);
        mallsMiscTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(id));
    }

    private void fallBackToDiskCache(final String url, final ImageView imageView)
    {
        Logger.print("dCache Falling back to DiskCache");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            { Logger.print("dCache inside Run");
                bitmap = diskCache.getBitmapFromDiskCache(url);

                Logger.print("dCache getting bitmap from cache"+url);

                if(bitmap != null)
                {
                    Logger.print("dCache found!:"+url);

                    memoryCache.addBitmapToCache(url, bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.print("dCache setting "+bitmap+ " on "+imageView);
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.print("dCache Force Downloading: "+url);
                                    BitmapForceDownloadTask bitmapDownloadTask = new BitmapForceDownloadTask
                                            (imageView, progressBar, rlHeader);
                                    bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                            url, String.valueOf(displayMetrics.widthPixels),
                                            String.valueOf(displayMetrics.heightPixels / 2));
                                }
                            });
                        }
                    });
                }
            }
        });

        thread.start();
    }

    View lastSelected = null;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_rate || viewId == R.id.tv_rate) {
            ratingDialog = showRatingDialog(this, "mall", id);
        } else if(viewId == R.id.iv_call || viewId == R.id.tv_call) {
            if(src != null && isNotNullOrEmpty(src.contact)) {
                String phoneNumber = src.contact.trim();
                if(phoneNumber.contains(","))
                    phoneNumber = phoneNumber.split(",")[0].trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(DIALER_PREFIX.concat(phoneNumber)));
                startActivity(intent);
            } else {
                makeText(getApplicationContext(), "No contact number found", LENGTH_LONG).show();
            }
        } else
        if(viewId == R.id.iv_share ) {
            shareBusiness(this, src.id);
        }
        else
        if(viewId == R.id.description_layout)
        {
            /*if(rlDescription.getTag().equals("collapsed"))
            {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescriptionArrow.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);

                rlDescription.setTag("expanded");
            }
            else
            {
                tvDescription.setVisibility(View.GONE);
                tvDescriptionArrow.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_next_arrow, 0);

                rlDescription.setTag("collapsed");
            }*/
        }
        else
        if(viewId == R.id.menu_layout)
        {
            if(v.getTag().equals("collapsed"))
            {
                llMenu.setVisibility(View.VISIBLE);
                tvMenuArrow.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);

                v.setTag("expanded");
            }
            else
            {
                llMenu.setVisibility(View.GONE);
                tvMenuArrow.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_next_arrow, 0);

                v.setTag("collapsed");
            }
        }
        else
        if(viewId == R.id.directions)
        {
            startDirections();
        }
        else
            if(viewId == R.id.deals)
            {
                adapter.expandStateMap.clear();

                groupList.clear();
                childList.clear();

                adapter.notifyDataSetChanged();

                for(MallDeals deal : response.result.deals)
                {
                    groupList.add(deal.category);
                    childList.add(deal.deals);
                }

                lastSelected.setSelected(false);

                btDeals.setSelected(true);
                lastSelected = btDeals;

                adapter.notifyDataSetChanged();

                expandableListView.smoothScrollToPositionFromTop(1, btDeals.getHeight() * 2
                        + (int) getResources().getDimension(R.dimen._9sdp), 200);
            }
    }

    public static void shareBusiness(Activity activity, int businessId) {
        Intent intent = new Intent();
        intent.setAction(ACTION_BUSINESS_DETAIL);

        String uri = intent.toUri(0);

        Logger.print("Uri: " + uri);

        uri = "View this restaurant on BizStore http://ooredoo.bizstore/business_detail?id=" + businessId+
                "\n\nor download app from play.google.com/store/apps/details?id="+packageName;

        DealDetailActivity.startShareIntent(activity, uri, businessId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ratingDialog != null && ratingDialog.isShowing()) {
            ratingDialog.dismiss();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setShowHideAnimationEnabled(false);
    }

    MallMiscResponse response;

    public void populateMisc(MallMiscResponse response)
    {
        if(response.result.deals.size() > 0)
        {
            groupList.clear();
            childList.clear();

            for(MallDeals deal : response.result.deals)
            {
                groupList.add(deal.category);
                childList.add(deal.deals);
            }

            adapter.notifyDataSetChanged();

//            btDeals.setSelected(true);
//            lastSelected = btDeals;
        }

        return;

       /* if( response.result.brands.size() > 0
                ||  response.result.deals.size() > 0)
        {
            llSimilarNearby.setVisibility(View.VISIBLE);

            this.response = response;

            if(response.result.brands.size() > 0)
            {
                groupList.clear();
                childList.clear();

                for(MallBrands brand : response.result.brands)
                {
                    groupList.add(brand.category);
                    childList.add(brand.brands);
                }

                btBrands.setSelected(true);
                lastSelected = btBrands;

                //adapter.notifyDataSetChanged();
            }
            else
            {
                btBrands.setVisibility(View.GONE);
            }

            if(response.result.deals.size() > 0)
            {
                if(response.result.brands.size() < 1) {

                    groupList.clear();
                    childList.clear();

                    for(MallDeals deal : response.result.deals)
                    {
                        groupList.add(deal.category);
                        childList.add(deal.deals);
                    }

                    btDeals.setSelected(true);
                    lastSelected = btDeals;
                }
            }
            else
            {
                btDeals.setVisibility(View.GONE);
            }

            //setupDealsAndBrands(response);
        }*/

    }

    private void startDirections()
    {
        double mLat = HomeActivity.lat;
        double mLong = HomeActivity.lng;

        String src = null, dest = null;

        // src = "saddr=" + mLat + "," + mLong + "&";

        // dest = "daddr="+mBusiness.latitude + "," + mBusiness.longitude;

        if(mLat != 0 && mLong != 0)
        {
            src = "saddr=" + mLat + "," + mLong + "&";
        }

        if(mBusiness.latitude != 0 && mBusiness.longitude != 0)
        {
            dest = "daddr="+mBusiness.latitude + "," + mBusiness.longitude;
        }

        String uri = "http://maps.google.com/maps?";

        // uri += src;
        // uri += dest;

        if(src != null)
        {
            uri += src;
        }

        if(dest != null)
        {
            uri += dest;
        }

        if(src == null)
        {
            Toast.makeText(this, R.string.direction_no_loc, Toast.LENGTH_SHORT).show();

            return;
        }

        if(dest == null)
        {
            Toast.makeText(this, R.string.business_loc_not_available, Toast.LENGTH_SHORT).show();

            return;
        }

        System.out.println("Directions URI:"+uri);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        try
        {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}