package com.ooredoo.bizstore.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.ScrollViewHelper;

import static com.ooredoo.bizstore.AppConstant.ACTION_DEAL_DETAIL;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.ui.activities.DealDetailActivity.startShareIntent;
import static com.ooredoo.bizstore.utils.DialogUtils.showRatingDialog;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class BusinessDetailActivity extends BaseActivity implements OnClickListener {

    public String category;
    public boolean showBanner = false;
    public boolean isFavorite = false;

    public int bannerResId = R.drawable.tmp_banner;
    private ActionBar mActionBar;

    private int id;

    private Dialog ratingDialog;
    public BusinessDetailActivity() {
        super();
        layoutResId = R.layout.activity_business_details;
    }

    @Override
    public void init() {
        setupToolbar();
        initViews();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_favorite) {
            //TODO update favorite business
        } else if(viewId == R.id.iv_rate) {
            ratingDialog = showRatingDialog(this, "business", id);
        } else if(viewId == R.id.iv_call) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:03352899951")); //TODO replace number
            startActivity(intent);
        } else if(viewId == R.id.iv_share) {
            //TODO implement share functionality
            Intent intent = new Intent();
            intent.setAction(ACTION_DEAL_DETAIL);

            String uri = intent.toUri(0);

            Logger.print("Uri: " + uri);

            uri = "View this awesome Restaurant on BizStore http://ooredoo.bizstore/business_detail?id=" + id;

            startShareIntent(this, uri, id);
        }
    }

    private void initViews() {
        id = intent.getIntExtra(AppConstant.ID, 0);
        category = intent.getStringExtra(CATEGORY);

        findViewById(R.id.iv_call).setOnClickListener(this);
        findViewById(R.id.iv_rate).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.iv_favorite).setOnClickListener(this);

        ScrollViewHelper scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);
        scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));
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
        mActionBar.setTitle("Charles & Keith");
    }
}