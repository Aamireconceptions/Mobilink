package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.DealDetailTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ScrollViewHelper;

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
    public boolean showBanner = false;

    public int bannerResId = R.drawable.tmp_banner;
    private ActionBar mActionBar;

    ScrollViewHelper scrollViewHelper;

    private int id;

    public Deal src;

    public static GenericDeal selectedDeal;

    private Dialog ratingDialog;

    public DealDetailActivity() {
        super();
        layoutResId = R.layout.activity_deal_details;
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

        Logger.logI("DEAL_DETAIL", "onNewIntent");

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

    private void initViews() {
        id = intent.getIntExtra(AppConstant.ID, 0);

        Logger.logI("DETAIL_ID", valueOf(id));
        category = intent.getStringExtra(CATEGORY);

        scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);
        scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));
        if(id > 0) {
            DealDetailTask dealDetailTask = new DealDetailTask(this, null);
            dealDetailTask.execute(String.valueOf(id));
        }
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        ((ImageView) findViewById(R.id.iv_deal_banner)).setImageResource(bannerResId);

        findViewById(R.id.tv_call).setOnClickListener(this);
        findViewById(R.id.iv_call).setOnClickListener(this);
        findViewById(R.id.tv_rate).setOnClickListener(this);
        findViewById(R.id.iv_rate).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_favorite).setOnClickListener(this);

        findViewById(R.id.tv_hdr_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);
        findViewById(R.id.iv_deal_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);

        findViewById(R.id.ll_deal_details).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_deal_category).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_banner_preview).setVisibility(View.VISIBLE);

        if(isNotNullOrEmpty(category)) {
            if(category.equalsIgnoreCase("PROMO") || category.equalsIgnoreCase("FEATURED")) {
                findViewById(R.id.ll_banner_preview).setVisibility(View.VISIBLE);
            }
        }
    }

    public void populateData(GenericDeal deal) {
        if(deal != null) {
            src = new Deal(deal);
            src.id = id;
            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, "deals", id);
            incrementViewsTask.execute();
            mActionBar.setTitle(deal.title);
            scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));

            if(isNotNullOrEmpty(deal.category) && category.contains(".")) {
                category = category.replace(".", ",");
            }
            ((TextView) findViewById(R.id.tv_title)).setText(deal.title);
            ((TextView) findViewById(R.id.tv_contact)).setText(deal.contact);
            ((TextView) findViewById(R.id.tv_deal_desc)).setText(deal.description);
            ((TextView) findViewById(R.id.tv_address)).setText(deal.address);
            ((TextView) findViewById(R.id.tv_category)).setText(deal.category);
            ((RatingBar) findViewById(R.id.rating_bar)).setRating(deal.rating);
            ((TextView) findViewById(R.id.tv_views)).setText(valueOf(deal.views));

            String discount = valueOf(deal.discount) + getString(R.string.percentage_off);
            ((TextView) findViewById(R.id.tv_discount)).setText(discount);
            ((TextView) findViewById(R.id.tv_deal_discount)).setText(discount);

            src.isFavorite = Deal.isFavorite(id);
            findViewById(R.id.iv_favorite).setSelected(src.isFavorite);

            int categoryIcon = getCategoryIcon(deal.category);

            ((ImageView) findViewById(R.id.iv_deal_category)).setImageResource(categoryIcon);

            ImageView ivDetail = (ImageView) findViewById(R.id.detail_img);

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            String detailImageUrl = deal.image.detailBannerUrl;

            Logger.print("detailImgUrl: " + detailImageUrl);

            if(detailImageUrl != null && !detailImageUrl.equals(""))
            {
                String imgUrl = BaseAsyncTask.IMAGE_BASE_URL + detailImageUrl;

                MemoryCache memoryCache = MemoryCache.getInstance();

                Bitmap bitmap = memoryCache.getBitmapFromCache(imgUrl);

                if(bitmap != null)
                {
                    ivDetail.setImageBitmap(bitmap);
                }
                else
                {
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

                    BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(ivDetail, progressBar);
                    bitmapDownloadTask.execute(imgUrl, String.valueOf(displayMetrics.widthPixels),
                                               String.valueOf(displayMetrics.heightPixels / 2));
                }


            }


        } else {
            makeText(getApplicationContext(), "No detail found", LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_favorite) {
            if(src != null) {
                src.isFavorite = !src.isFavorite;
                v.setSelected(src.isFavorite);
                Deal.updateDealAsFavorite(src);
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

            if(src != null) // TODO remove this and make sure src never get null
            shareDeal(this, src.id);
        }
    }

    public static void shareDeal(Activity activity, long dealId) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DEAL_DETAIL);

        String uri = intent.toUri(0);

        Logger.print("Uri: " + uri);

        uri = "View this awesome deal on BizStore http://ooredoo.bizstore/deal_detail?id=" + dealId;

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
}