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
import com.ooredoo.bizstore.asynctasks.BusinessDetailTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Favorite;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ScrollViewHelper;
import com.ooredoo.bizstore.utils.SnackBarUtils;

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

    public String category;

    private ActionBar mActionBar;

    ScrollViewHelper scrollViewHelper;

    private int id = -1;

    public Business src;

    public static Business selectedBusiness;

    private Dialog ratingDialog;

    public BusinessDetailActivity() {
        super();
        layoutResId = R.layout.activity_business_details;
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

    private void initViews() {
        /*id = intent.getIntExtra(AppConstant.ID, 0);

        Logger.logI("DETAIL_ID", valueOf(id));*/

        src = (Business) intent.getSerializableExtra("business");

        category = intent.getStringExtra(CATEGORY);

        scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);
        scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));

        SnackBarUtils snackBarUtils = new SnackBarUtils(this, findViewById(R.id.root));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        findViewById(R.id.tv_call).setOnClickListener(this);
        findViewById(R.id.iv_call).setOnClickListener(this);
        findViewById(R.id.tv_rate).setOnClickListener(this);
        findViewById(R.id.iv_rate).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_favorite).setOnClickListener(this);

        if(src != null && id != -1) {

            BusinessDetailTask detailTask = new BusinessDetailTask(this, null, snackBarUtils);
            detailTask.execute(String.valueOf(id));
        }
        else
        {
            populateData(src);
        }
    }

    public void populateData(Business business) {
        if(business != null) {
            src = business;
            src.id = business.id;

            src.isFavorite = Favorite.isFavorite(src.id);

            findViewById(R.id.iv_favorite).setSelected(src.isFavorite);
            IncrementViewsTask incrementViewsTask = new IncrementViewsTask(this, "business", id);
            incrementViewsTask.execute();
            if(isNotNullOrEmpty(business.title)) {
                mActionBar.setTitle(business.title);
                scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));
            }

            ((TextView) findViewById(R.id.tv_desc)).setText(business.description);

            ((TextView) findViewById(R.id.tv_title)).setText(business.title);
            ((TextView) findViewById(R.id.tv_contact)).setText(business.contact);
            ((TextView) findViewById(R.id.tv_address)).setText(business.address);
            ((TextView) findViewById(R.id.tv_location)).setText(business.location);
            ((RatingBar) findViewById(R.id.rating_bar)).setRating(business.rating);
            ((TextView) findViewById(R.id.tv_views)).setText(valueOf(business.views));
            findViewById(R.id.iv_favorite).setSelected(src.isFavorite);

            ImageView ivDetail = (ImageView) findViewById(R.id.detail_img);

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

            String detailImageUrl = null;

            if(business.image != null)
            {
               detailImageUrl = business.image.detailBannerUrl;
            }

            Logger.print("detailImgUrl: "+detailImageUrl);

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
                boolean isFavorite = !v.isSelected();
                v.setSelected(isFavorite);
                src.isFavorite = isFavorite;
                Favorite favorite = new Favorite(src);
                favorite.isBusiness = true;
                Favorite.updateFavorite(favorite);
                favorite.save();
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
        } else if(viewId == R.id.iv_share || viewId == R.id.tv_share) {
            shareBusiness(this, src.id);
        }
    }

    public static void shareBusiness(Activity activity, int businessId) {
        Intent intent = new Intent();
        intent.setAction(ACTION_BUSINESS_DETAIL);

        String uri = intent.toUri(0);

        Logger.print("Uri: " + uri);

        uri = "View this restaurant on BizStore http://ooredoo.bizstore/business_detail?id=" + businessId;

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
}