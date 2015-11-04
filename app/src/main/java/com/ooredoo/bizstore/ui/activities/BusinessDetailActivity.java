package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.BusinessAdapter;
import com.ooredoo.bizstore.adapters.Gallery;
import com.ooredoo.bizstore.adapters.GalleryStatePagerAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BusinessDetailTask;
import com.ooredoo.bizstore.asynctasks.BusinessMiscTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.Home;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.model.Menu;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ScrollViewHelper;
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
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class BusinessDetailActivity extends BaseActivity implements OnClickListener {

    Bitmap bitmap;
    public String category;

    private ActionBar mActionBar;

    ScrollViewHelper scrollViewHelper;

    ExpandableListView expandableListView;

    private int id = -1;

    public Business src;

    MemoryCache memoryCache = MemoryCache.getInstance();

    DiskCache diskCache = DiskCache.getInstance();
    public static Business selectedBusiness;

    private Dialog ratingDialog;

    static String packageName;

    public BusinessDetailActivity() {
        super();
        layoutResId = R.layout.business_detail_activity;
    }

    @Override
    public void init() {
        setupToolbar();
        initViews();
        handleIntentFilter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.logI("BUSINESS_DETAIL", "onNewIntent");

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
                id = intent.getIntExtra(AppConstant.ID, 0);
                getIntent().putExtra(AppConstant.ID, Integer.parseInt(paramId));
                getIntent().putExtra(CATEGORY, DEAL_CATEGORIES[0]);
                initViews();
            }
        }
    }

    View header;
    SnackBarUtils snackBarUtils;
    private void initViews() {
        /*id = intent.getIntExtra(AppConstant.ID, 0);

        Logger.logI("DETAIL_ID", valueOf(id));*/

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        header = layoutInflater.inflate(R.layout.fragment_business_details, null);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);
        expandableListView.addHeaderView(header);

        packageName = getPackageName();
        src = (Business) intent.getSerializableExtra("business");

        category = intent.getStringExtra(CATEGORY);

        //scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);
       // scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));

        snackBarUtils = new SnackBarUtils(this, findViewById(R.id.root));

        ProgressBar progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        header.findViewById(R.id.tv_call).setOnClickListener(this);
        header.findViewById(R.id.iv_call).setOnClickListener(this);
        header.findViewById(R.id.tv_rate).setOnClickListener(this);
        header.findViewById(R.id.iv_rate).setOnClickListener(this);
        header.findViewById(R.id.tv_share).setOnClickListener(this);
        header.findViewById(R.id.iv_share).setOnClickListener(this);

        findViewById(R.id.iv_favorite).setOnClickListener(this);

        if(src != null && id != -1)
        {
            BusinessDetailTask detailTask = new BusinessDetailTask(this, null, snackBarUtils);
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

    RelativeLayout rlDescription;

    TextView tvDescription, tvDescriptionArrow, tvMenuArrow;

    LinearLayout llMenu;

    ViewPager galleryPager;

    BusinessAdapter adapter;

    Business mBusiness;

    public void populateData(Business business) {
        if(business != null) {

            id = business.id;
            mBusiness = business;
            LinearLayout llDirections = (LinearLayout) findViewById(R.id.directions_layout);

            RelativeLayout rlDistance = (RelativeLayout) findViewById(R.id.distance_layout);

            if((business.latitude == 0 && business.longitude == 0)
                    || (HomeActivity.lat == 0 && HomeActivity.lng == 0))
            {
                llDirections.setVisibility(View.GONE);
                rlDistance.setVisibility(View.GONE);
            }
            else
            {
                float results[] = new float[3];
                Location.distanceBetween(HomeActivity.lat, HomeActivity.lng,
                        business.latitude, business.longitude,
                        results);

                TextView tvDirections = (TextView) findViewById(R.id.directions);
                tvDirections.setText(results[0] + "km");
                tvDirections.setOnClickListener(this);

                TextView tvDistance= (TextView) findViewById(R.id.distance);
                tvDistance.setText(results[0] + "km away");
            }

            TextView tvTiming = (TextView) findViewById(R.id.timing);
            tvTiming.setText(business.timing);


            ImageView ivBrandLogo = (ImageView) findViewById(R.id.brand_logo);

            String brandLogo = null;

            if(business.image != null)
            {
                brandLogo = business.image.logoUrl;
            }

            Logger.print("BrandLogo: "+brandLogo);

            if(brandLogo != null && !brandLogo.equals(""))
            {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + brandLogo;

                bitmap = memoryCache.getBitmapFromCache(imgUrl);

                if(bitmap != null)
                {
                    ivBrandLogo.setImageBitmap(bitmap);
                }
                else
                {
                    fallBackToDiskCache(imgUrl, ivBrandLogo);
                }
            }

            groupList = new ArrayList<>();

            childList = new ArrayList<>();

            adapter = new BusinessAdapter(this, groupList, childList);

            expandableListView.setAdapter(adapter);

            src = business;
            src.id = business.id;

            src.isFavorite = Favorite.isFavorite(src.id);

            findViewById(R.id.iv_favorite).setSelected(src.isFavorite);
            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, "business", id);
            incrementViewsTask.execute();
            if(isNotNullOrEmpty(business.title)) {
                mActionBar.setTitle(business.title);
                //scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));
            }

            tvDescriptionArrow = (TextView) header.findViewById(R.id.description_arrow);

            rlDescription = (RelativeLayout) header.findViewById(R.id.description_layout);
            rlDescription.setOnClickListener(this);

            tvDescription = ((TextView) header.findViewById(R.id.description));
            tvDescription.setText(business.description);

            RelativeLayout rlMenu = (RelativeLayout) header.findViewById(R.id.menu_layout);
            rlMenu.setOnClickListener(this);

            tvMenuArrow = (TextView) header.findViewById(R.id.menu_arrow);

            llMenu = (LinearLayout) header.findViewById(R.id.ll_menu);

            ((TextView) header.findViewById(R.id.tv_title)).setText(business.title);
            ((TextView) header.findViewById(R.id.phone)).setText(business.contact);
            ((TextView) header.findViewById(R.id.address)).setText(business.address);
            ((TextView) header.findViewById(R.id.city)).setText(business.location);
            TextView tvType = (TextView) header.findViewById(R.id.type);
            if(business.type != null)
            {
                tvType.setText(business.type);
            }
            else
            {
                tvType.setVisibility(View.GONE);
            }

            ((RatingBar) header.findViewById(R.id.rating_bar)).setRating(business.rating);
            ((TextView) header.findViewById(R.id.tv_views)).setText(valueOf(business.views));
           findViewById(R.id.iv_favorite).setSelected(src.isFavorite);

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

        BusinessMiscTask businessMiscTask = new BusinessMiscTask(this, null, snackBarUtils);
        businessMiscTask.execute(String.valueOf(id));
    }




    private void fallBackToDiskCache(final String url, final ImageView imageView)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                bitmap = diskCache.getBitmapFromDiskCache(url);

                Logger.print("dCache getting bitmap from cache");

                if(bitmap != null)
                {
                    Logger.print("dCache found!");

                    memoryCache.addBitmapToCache(url, bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                                    BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(imageView, progressBar);
                        /*bitmapDownloadTask.execute(imgUrl, String.valueOf(displayMetrics.widthPixels),
                                String.valueOf(displayMetrics.heightPixels / 2));*/
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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_favorite) {
            if(src != null) {
                boolean isFavorite = !v.isSelected();
                v.setSelected(isFavorite);
                src.isFavorite = isFavorite;
                Favorite favorite = new Favorite(src);
                favorite.isBusiness = 1;
                Favorite.updateFavorite(favorite);
            }
        } else if(viewId == R.id.iv_rate || viewId == R.id.tv_rate) {
            ratingDialog = showRatingDialog(this, "business", id);
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
        if(viewId == R.id.iv_share || viewId == R.id.tv_share) {
            shareBusiness(this, src.id);
        }
        else
            if(viewId == R.id.description_layout)
            {
                if(rlDescription.getTag().equals("collapsed"))
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
                }
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

    public void populateMisc(Business business)
    {
        llMenu = (LinearLayout) header.findViewById(R.id.ll_menu);

        setupMenu(llMenu, business);

        setupGallery(business);

        setupSimilarDealsAndBrands(business);
    }

    private void setupMenu(LinearLayout llMenu, Business business)
    {
        View menuView = getLayoutInflater().inflate(R.layout.layout_menu, null);

        if(business.menus != null)
        {
            for(Menu menu : business.menus)
            {
                TextView textView = (TextView) menuView.findViewById(R.id.item);
                textView.setText(menu.item);

                TextView tvPrice = (TextView) menuView.findViewById(R.id.price);
                tvPrice.setText(menu.price);

                llMenu.addView(menuView);
            }
        }
    }

    private void setupGallery(Business business)
    {
        LinearLayout llGallery = (LinearLayout) header.findViewById(R.id.gallery_layout);

        if(business.galleries != null && business.galleries.size() > 0)
        {
            llGallery.setVisibility(View.VISIBLE);

            GalleryStatePagerAdapter galleryAdapter = new GalleryStatePagerAdapter(getFragmentManager(), business.galleries);

            galleryPager = (ViewPager) header.findViewById(R.id.gallery_pager);
            galleryPager.setAdapter(galleryAdapter);
        }
        else
        {
            llGallery.setVisibility(View.GONE);
        }
    }

    public void setupSimilarDealsAndBrands(Business business)
    {
        if(business.moreDeals.size() > 0)
        {
            groupList.add(business.title + " Deals");
            childList.add(( business.moreDeals));
        }

        if(business.similarBrands.size() > 0)
        {
            groupList.add("Similar brands");
            childList.add(business.similarBrands);
        }

        adapter.notifyDataSetChanged();
    }

    private void startDirections()
    {
        double mLat = HomeActivity.lat;
        double mLong = HomeActivity.lng;

        String src = null, dest = null;

        src = "saddr=" + mLat + "," + mLong + "&";

        dest = "daddr="+mBusiness.latitude + "," + mBusiness.longitude;

        String uri = "http://maps.google.com/maps?";

        uri += src;
        uri += dest;

        System.out.println("Directions URI:"+uri);

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));

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