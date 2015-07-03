package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealDetailTask;
import com.ooredoo.bizstore.asynctasks.IncrementViewsTask;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.ScrollViewHelper;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.ooredoo.bizstore.AppConstant.ACTION_DEAL_DETAIL;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
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

    private long id;

    private Deal src;

    public DealDetailActivity() {
        super();
        layoutResId = R.layout.activity_deal_details;
    }

    @Override
    public void init() {
        setupToolbar();
        initViews();
    }

    private void initViews() {
        id = intent.getLongExtra(AppConstant.ID, 0);
        category = intent.getStringExtra(CATEGORY);

        scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);
        scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));
        if(id > 0) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            DealDetailTask dealDetailTask = new DealDetailTask(this, progressBar);
            dealDetailTask.execute(String.valueOf(id));
            scrollViewHelper.setAlpha(0.4f);
        }
        ((ImageView) findViewById(R.id.iv_deal_banner)).setImageResource(bannerResId);

        findViewById(R.id.iv_call).setOnClickListener(this);
        findViewById(R.id.iv_rate).setOnClickListener(this);
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
            ((TextView) findViewById(R.id.tv_title)).setText(deal.title);
            ((TextView) findViewById(R.id.tv_contact)).setText(deal.contact);
            ((TextView) findViewById(R.id.tv_deal_desc)).setText(deal.detail);
            ((TextView) findViewById(R.id.tv_address)).setText(deal.address);
            ((TextView) findViewById(R.id.tv_location)).setText(deal.address);
            ((TextView) findViewById(R.id.tv_category)).setText(deal.category);
            ((RatingBar) findViewById(R.id.rating_bar)).setRating(deal.rating);
            ((TextView) findViewById(R.id.tv_views)).setText(valueOf(deal.views));
            ((TextView) findViewById(R.id.tv_discount)).setText(valueOf(deal.discount));
            scrollViewHelper.setAlpha(1f);
            src.isFavorite = Deal.isFavorite(id);
            findViewById(R.id.iv_favorite).setSelected(src.isFavorite);
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
        } else if(viewId == R.id.iv_rate) {
            showRatingDialog(this);
        } else if(viewId == R.id.iv_call) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:03352899951")); //TODO replace number
            startActivity(intent);
        } else if(viewId == R.id.iv_share) {
            //TODO implement share functionality
            Intent intent = new Intent(ACTION_DEAL_DETAIL);
            startActivity(intent);
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