package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.DealDetailMiscTask;
import com.ooredoo.bizstore.asynctasks.DealDetailTask;
import com.ooredoo.bizstore.asynctasks.GetCodeTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.SharedPrefUtils;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.ACTION_DEAL_DETAIL;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;
import static com.ooredoo.bizstore.AppConstant.DIALER_PREFIX;
import static com.ooredoo.bizstore.utils.CategoryUtils.getCategoryIcon;
import static com.ooredoo.bizstore.utils.DialogUtils.showRatingDialog;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static java.lang.String.valueOf;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class DealDetailActivity extends BaseActivity implements OnClickListener {

    public String category;
    static String packageName;
    public boolean showBanner = false;

   // public int bannerResId = R.drawable.tmp_banner;
    private ActionBar mActionBar;

    //ScrollViewHelper scrollViewHelper;

    private Button btGetCode;

    private ImageView ivLine;

    private LinearLayout llVoucherCode;

    private TextView tvDiscount, tvValidity, tvCode, tvNote, tvDiscountVoucher;

    private int id = -1;

    public Deal src;

    boolean isFav = false;

    public static GenericDeal selectedDeal, genericDeal;

    private Dialog ratingDialog;

    private  MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private SnackBarUtils snackBarUtils;

    LinearLayout llSimilarNearby;

    public DealDetailActivity() {
        super();
        layoutResId = R.layout.deal_detail_activity;
    }

    @Override
    public void init() {

        diskCache.requestInit(this);

        setupToolbar();

        handleIntentFilter();

        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.print("DEAL_DETAIL: onNewIntent");

        setIntent(intent);

        handleIntentFilter();
    }

    private void handleIntentFilter() {
        Intent intent = getIntent();

        Logger.print("HandleIntentFilter");

        if(intent != null) {
            Uri uri = intent.getData();

            /*if(uri != null) {
                String paramId = uri.getQueryParameter("id");

                Logger.print("Extras: " + paramId);
                id = intent.getIntExtra(AppConstant.ID, 0);
                getIntent().putExtra(AppConstant.ID, Integer.parseInt(paramId));
                getIntent().putExtra(CATEGORY, DEAL_CATEGORIES[0]);
                initViews();
            }*/

            if(uri != null)
            {
                String paramId = uri.getQueryParameter("id");

                Logger.print("Extras: " + paramId);

                getIntent().putExtra(AppConstant.ID, Integer.parseInt(paramId));
            }

            id = intent.getIntExtra(AppConstant.ID, 0);
            getIntent().putExtra(CATEGORY, DEAL_CATEGORIES[0]);
            //initViews();
        }
    }

    View header;
    ListView listView;
    ListViewBaseAdapter similarAdapter, nearbyAdapter;

    List<GenericDeal> similarDeals = new ArrayList<>(),  nearbyDeals = new ArrayList<>();
    private void initViews()
    {
        genericDeal = (GenericDeal) intent.getSerializableExtra("generic_deal");

        //  id = intent.getIntExtra(AppConstant.ID, 0);
packageName = getPackageName();
        if(genericDeal != null) {

            id = genericDeal.id;

            Logger.logI("DETAIL_ID", valueOf(id));
        }

        header = getLayoutInflater().inflate(R.layout.frag_deal_detail, null);

        llSimilarNearby = (LinearLayout) header.findViewById(R.id.similar_nearby);

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(header);
        listView.setAdapter(null);

        TextView tvEmptyView = (TextView) findViewById(R.id.empty_view);
        //listView.setEmptyView(tvEmptyView);

        category = intent.getStringExtra(CATEGORY);

        //scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);

        snackBarUtils = new SnackBarUtils(this, findViewById(R.id.root));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
       // ((ImageView) findViewById(R.id.iv_deal_banner)).setImageResource(bannerResId);

        header.findViewById(R.id.tv_call).setOnClickListener(this);
        header.findViewById(R.id.iv_call).setOnClickListener(this);
        header.findViewById(R.id.tv_rate).setOnClickListener(this);
        header.findViewById(R.id.iv_rate).setOnClickListener(this);
        header.findViewById(R.id.tv_share).setOnClickListener(this);
        header.findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_favorite).setOnClickListener(this);

        //header.findViewById(R.id.tv_hdr_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);
        //header.findViewById(R.id.iv_deal_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);

        //header.findViewById(R.id.ll_deal_details).setVisibility(View.VISIBLE);
        //header.findViewById(R.id.ll_deal_category).setVisibility(View.VISIBLE);
       // header.findViewById(R.id.ll_banner_preview).setVisibility(View.VISIBLE);

       /* if(isNotNullOrEmpty(category)) {
            if(category.equalsIgnoreCase("PROMO") || category.equalsIgnoreCase("FEATURED")) {
                findViewById(R.id.ll_banner_preview).setVisibility(View.VISIBLE);
            }
        }*/

        btGetCode = (Button) header.findViewById(R.id.get_code);
        btGetCode.setOnClickListener(this);

        //ivLine = (ImageView) findViewById(R.id.horizontal_line);

        //llVoucherCode = (LinearLayout) findViewById(R.id.voucher_code_layout);

        tvDiscount = (TextView) findViewById(R.id.discount);

       // tvValidity = (TextView) findViewById(R.id.validity);

        //tvCode = (TextView) findViewById(R.id.voucher_code);

       // tvNote = (TextView) findViewById(R.id.note);

        //tvDiscountVoucher = (TextView) findViewById(R.id.discount_voucher);

        if(genericDeal == null) {
            DealDetailTask dealDetailTask = new DealDetailTask(this, null, snackBarUtils);
            dealDetailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(id));
            //dealDetailTask.execute(String.valueOf(id));
        }
        else
        {
            populateData(genericDeal);
        }
    }

    ImageView ivDetail;
    ProgressBar progressBar;
    public void populateData(final GenericDeal deal) {
        if(deal != null) {
            src = new Deal(deal);
            src.id = deal.id;
            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, "deals", id);
            incrementViewsTask.execute();

            mActionBar.setTitle(deal.title);

            TextView tvBrand = (TextView) findViewById(R.id.brand_name);
            tvBrand.setText(deal.businessName);

            TextView tvValidity = (TextView) findViewById(R.id.validity);
            tvValidity.setText("This deal is valid till " + deal.endDate);

            LinearLayout llDirections = (LinearLayout) findViewById(R.id.directions_layout);
            llDirections.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDirections();
                }
            });

            RelativeLayout rlDistance = (RelativeLayout) findViewById(R.id.distance_layout);

            if((deal.latitude == 0 && deal.longitude == 0)
                    || (HomeActivity.lat == 0 && HomeActivity.lng == 0))
            {
                llDirections.setVisibility(View.GONE);
                rlDistance.setVisibility(View.GONE);
            }
            else
            {
                float results[] = new float[3];
                Location.distanceBetween(HomeActivity.lat, HomeActivity.lng,
                        deal.latitude, deal.longitude,
                        results);

                TextView tvDirections = (TextView) findViewById(R.id.directions);

                tvDirections.setText(String.format("%.1f",results[0] / 1000) + "km");
                tvDirections.setOnClickListener(this);

                TextView tvDistance= (TextView) findViewById(R.id.distance);
                tvDistance.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startDirections();
                    }
                });

                tvDistance.setText(String.format("%.2f",results[0]) + " km away");
            }

            //scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));

            if(isNotNullOrEmpty(deal.category) && deal.category.contains(".")) {
                deal.category = deal.category.replace(".", ",");
            }
            ((TextView) header.findViewById(R.id.title)).setText(deal.title);
            ((TextView) header.findViewById(R.id.phone)).setText(deal.contact);
            ((TextView) header.findViewById(R.id.description)).setText(deal.description);
            ((TextView) header.findViewById(R.id.address)).setText(deal.address);
            //((TextView) findViewById(R.id.tv_category)).setText(deal.category);
            ((RatingBar) header.findViewById(R.id.rating_bar)).setRating(deal.rating);
            header.findViewById(R.id.iv_views).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DealDetailActivity.this, "This deal has been viewed "+deal.views, Toast.LENGTH_SHORT).show();
                }
            });

            TextView tvView = ((TextView) header.findViewById(R.id.tv_views));
            tvView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DealDetailActivity.this, "This deal has been viewed "+deal.views, Toast.LENGTH_SHORT).show();
                }
            });
            tvView.setText(valueOf(deal.views));

            RelativeLayout rlVoucher = (RelativeLayout) header.findViewById(R.id.voucher_layout);

            if(deal.discount == 0) {
                rlVoucher.setVisibility(View.GONE);
                tvDiscount.setVisibility(View.GONE);

               // findViewById(R.id.tv_discount).setVisibility(View.GONE);
                //findViewById(R.id.tv_deal_discount).setVisibility(View.GONE);
            }
            else
            {
                rlVoucher.setVisibility(View.VISIBLE);
                tvDiscount.setVisibility(View.VISIBLE);
            }

            final ImageView ivBrandLogo = (ImageView) findViewById(R.id.brand_logo);

            String brandLogo = deal.businessLogo;

           /* if(deal.image != null)
            {
                brandLogo = deal.image.logoUrl;
            }*/

            Logger.print("BrandLogo: " + brandLogo);

            RelativeLayout rlTiming = (RelativeLayout) findViewById(R.id.timing_layout);

            TextView tvTiming = (TextView) findViewById(R.id.timing);

            if(deal.timing != null && !deal.timing.isEmpty())
            {
                rlTiming.setVisibility(View.VISIBLE);
                tvTiming.setText(deal.timing);
            }
            else
            {
                rlTiming.setVisibility(View.GONE);
            }

            if(brandLogo != null && !brandLogo.equals(""))
            {
               final String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + brandLogo;

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
            else
            {
                ivBrandLogo.setVisibility(View.GONE);
            }

            String discount = valueOf(deal.discount) + getString(R.string.percentage_off);
            //((TextView) findViewById(R.id.discount)).setText(discount);
            //((TextView) findViewById(R.id.tv_deal_discount)).setText(discount);

            src.isFavorite = Favorite.isFavorite(id);

            findViewById(R.id.iv_favorite).setSelected(src.isFavorite);

            int categoryIcon = getCategoryIcon(deal.category);

            //((ImageView) header.findViewById(R.id.iv_deal_category)).setImageResource(categoryIcon);

            ivDetail = (ImageView) header.findViewById(R.id.detail_img);

            progressBar = (ProgressBar) header.findViewById(R.id.progressBar);

            String detailImageUrl = null;
            if(deal.image != null)
            {
                detailImageUrl = deal.image.detailBannerUrl;
            }

            Logger.print("detailImgUrl: " + detailImageUrl);

            if(detailImageUrl != null && !detailImageUrl.equals(""))
            {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + detailImageUrl;

                Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                if(bitmap != null)
                {
                    ivDetail.setImageBitmap(bitmap);
                }
                else
                {
                    fallBackToDiskCache(imgUrl, ivDetail);
                }
            }


            tvDiscount.setText(discount);

            Button btSimilarDeals = (Button) header.findViewById(R.id.similar_deals);
            btSimilarDeals.setOnClickListener(this);
           // btSimilarDeals.performClick();

            Button btNearbyDeals = (Button) header.findViewById(R.id.nearby_deals);
            btNearbyDeals.setOnClickListener(this);

            //tvValidity.setText(getString(R.string.redeem_until) + " " + deal.endDate);

            /*if(deal.voucher != null && !deal.voucher.isEmpty())
            {
                llVoucherCode.setVisibility(View.VISIBLE);

                if(deal.status.equals("Available"))
                {
                    tvCode.setText(deal.voucher);

                    tvNote.setVisibility(View.VISIBLE);
                }
                else
                {
                    tvCode.setText("This voucher code has been already redeemed!");

                    tvDiscountVoucher.setVisibility(View.GONE);
                }

                btGetCode.setVisibility(View.GONE);

                ivLine.setVisibility(View.VISIBLE);
            }*/

            if(deal.is_exclusive == 1)
            {
               // header.findViewById(R.id.voucher_frame).setVisibility(View.VISIBLE);
            }

        } else {
            makeText(getApplicationContext(), "No detail found", LENGTH_LONG).show();
        }


      similarAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, similarDeals, null);

        nearbyAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, nearbyDeals, null);
        //listView.setAdapter(similarAdapter);
        //listView.setAdapter(nearbyAdapter);


        DealDetailMiscTask detailMiscTask = new DealDetailMiscTask(this, similarDeals, nearbyDeals);
        detailMiscTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(deal.id));
    }
    Bitmap bitmap;

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
                            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

                            BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(ivDetail, progressBar);
                        /*bitmapDownloadTask.execute(imgUrl, String.valueOf(displayMetrics.widthPixels),
                                String.valueOf(displayMetrics.heightPixels / 2));*/
                            bitmapDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    url, String.valueOf(displayMetrics.widthPixels),
                                    String.valueOf(displayMetrics.heightPixels / 2));
                        }
                    });

                }
            }
        });

        thread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:

                sendResult();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    View lastSelected = null;
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_favorite) {
            if(genericDeal != null) {
                genericDeal.isFav = !genericDeal.isFav;
                v.setSelected(genericDeal.isFav);
                Favorite favorite = new Favorite(genericDeal);
                Favorite.updateFavorite(favorite);

                isFav = genericDeal.isFav;

            } else {
                //TODO src == null => No detail found OR any exception occurred.
            }
        } else if(viewId == R.id.iv_rate || viewId == R.id.tv_rate) {
            ratingDialog = showRatingDialog(this, "deals", id);
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
        } else if(viewId == R.id.iv_share || viewId == R.id.tv_share) {

            shareDeal(this, id);
        }
        else
        if(viewId == R.id.get_code)
        {
            GetCodeTask getCodeTask = new GetCodeTask(this, snackBarUtils);
            getCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(id));
        }
        else
            if(viewId == R.id.similar_deals)
            {
                setSelected(v);
                similarAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, similarDeals, null);
                similarAdapter.setListingType("deals");

               listView.setAdapter(similarAdapter);

               // listView.scrollTo(0, (int) llSimilarNearby.getY());
                //listView.setScrollY((int) llSimilarNearby.getY());
               // listView.smoothScrollToPosition(1);

                listView.setSelection(1);
//listView.requestFocusFromTouch();

                //similarAdapter.notifyDataSetChanged();

            }
        else
                if(viewId == R.id.nearby_deals)
                {
                    if(HomeActivity.lat != 0 && HomeActivity.lng != 0)
                    {
                        if(nearbyDeals.size() > 0)
                        {
                            setSelected(v);

                            nearbyAdapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, nearbyDeals, null);
                            nearbyAdapter.setListingType("deals");

                            listView.setAdapter(nearbyAdapter);

                            listView.setSelection(1);
                            //listView.scrollBy(0,  llSimilarNearby.getTop() - 100);

                        }
                        else
                        {
                            Toast.makeText(this, "No Nearby Deals Available!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(this, "Your location is not available!", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                if(viewId == R.id.directions)
                {
                    startDirections();
                }
    }

    private void setSelected(View v)
    {
        if(lastSelected != null)
        {
            lastSelected.setSelected(false);
        }

        v.setSelected(true);

        lastSelected = v;
    }

    public void showCode(String code)
    {
        btGetCode.setVisibility(View.GONE);

        ivLine.setVisibility(View.VISIBLE);

        llVoucherCode.setVisibility(View.VISIBLE);

        tvCode.setText(code);

        tvNote.setVisibility(View.VISIBLE);

        genericDeal.voucher = code;
    }

    public static void shareDeal(Activity activity, long dealId) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DEAL_DETAIL);

        String uri = intent.toUri(0);

        Logger.print("Uri: " + uri);

        uri = "View this awesome deal on BizStore http://ooredoo.bizstore.deal/deal_detail?id=" + dealId +
        "\n\nor download app from play.google.com/store/apps/details?id="+packageName ;

        startShareIntent(activity, uri, dealId);
    }

    private void startDirections()
    {
        double mLat = HomeActivity.lat;
        double mLong = HomeActivity.lng;

        String src = null, dest = null;

        if(mLat != 0 && mLong != 0)
        {
            src = "saddr=" + mLat + "," + mLong + "&";
        }

        if(genericDeal.latitude != 0 && genericDeal.longitude != 0)
        {
            dest = "daddr="+genericDeal.latitude + "," + genericDeal.longitude;
        }

        String uri = "http://maps.google.com/maps?";

        if(src != null)
        {
            uri += src;
        }

        if(dest != null)
        {
            uri += dest;
        }

        System.out.println("Directions URI:"+uri);

        if(src == null)
        {
            Toast.makeText(this, "Location not available. Please enable location services!", Toast.LENGTH_SHORT).show();

            return;
        }

        if(dest == null)
        {
            Toast.makeText(this, "Deal location is not available!", Toast.LENGTH_SHORT).show();

            return;
        }

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

    public static void startShareIntent(Activity activity, String uri, long id) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(AppConstant.ID, id);
        intent.putExtra(Intent.EXTRA_TEXT, uri);

        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {

        if(ratingDialog != null && ratingDialog.isShowing()) {
            ratingDialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        sendResult();
    }

    private void sendResult()
    {
        Intent intent = new Intent();
        intent.putExtra("is_fav", isFav);
        if(genericDeal != null)
        {
            intent.putExtra("voucher", genericDeal.voucher);

            intent.putExtra("views", genericDeal.views);
        }

        setResult(RESULT_OK, intent);

        finish();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setShowHideAnimationEnabled(false);
    }

   public void onHaveData()
   {
       llSimilarNearby.setVisibility(View.VISIBLE);
   }

    public void onNoData()
    {
        llSimilarNearby.setVisibility(View.GONE);
    }
}