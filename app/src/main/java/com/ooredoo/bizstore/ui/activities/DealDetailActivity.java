package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.DealDetailTask;
import com.ooredoo.bizstore.asynctasks.GetCodeTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ScrollViewHelper;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.io.IOException;
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
    ListViewBaseAdapter adapter;

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

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(header);

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
    public void populateData(GenericDeal deal) {
        if(deal != null) {
            src = new Deal(deal);
            src.id = deal.id;
            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, "deals", id);
            incrementViewsTask.execute();

            mActionBar.setTitle(deal.title);

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
            ((TextView) header.findViewById(R.id.tv_views)).setText(valueOf(deal.views));

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
                    fallBackToDiskCache(imgUrl);
                }
            }

            tvDiscount.setText(discount);

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


      adapter = new ListViewBaseAdapter(this, R.layout.list_deal_promotional, similarDeals, null);
        listView.setAdapter(adapter);
    }
    Bitmap bitmap;

    private void fallBackToDiskCache(final String url)
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
                            ivDetail.setImageBitmap(bitmap);
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


}