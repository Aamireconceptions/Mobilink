package com.ooredoo.bizstore.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.ScrollViewListener;
import com.ooredoo.bizstore.utils.ScrollViewHelper;

import static com.ooredoo.bizstore.AppConstant.ACTION_DEAL_DETAIL;
import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.utils.DialogUtils.showRatingDialog;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class DealDetailActivity extends BaseActivity implements OnClickListener {

    public String category;
    public boolean showBanner = false;
    public boolean isFavorite = false;

    public int bannerResId = R.drawable.tmp_banner;
    private ActionBar mActionBar;

    private long id;
    public DealDetailActivity() {
        super();
        layoutResId = R.layout.activity_deal_details;
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
            isFavorite = !isFavorite;
            int favDrawable = isFavorite ? R.drawable.ic_like_big_active : R.drawable.ic_like_big_inactive;
            ((ImageView) findViewById(R.id.iv_favorite)).setImageResource(favDrawable);
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

    private void initViews() {
        id = intent.getLongExtra(AppConstant.ID, 0);
        category = intent.getStringExtra(CATEGORY);
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
        ScrollViewHelper scrollViewHelper = (ScrollViewHelper) findViewById(R.id.scrollViewHelper);
        scrollViewHelper.setOnScrollViewListener(new ScrollViewListener(mActionBar));
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