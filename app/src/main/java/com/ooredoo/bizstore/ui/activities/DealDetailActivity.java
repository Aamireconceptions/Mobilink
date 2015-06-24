package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.ImageView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.utils.Converter;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class DealDetailActivity extends BaseActivity implements OnClickListener, OnScrollChangedListener, View.OnTouchListener {
    public boolean showBanner = false;
    public boolean isFavorite = false;
    public int bannerResId = R.drawable.tmp_banner;
    private float mActionBarHeight;
    private ActionBar mActionBar;

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
        }
    }

    private void initViews() {
        ((ImageView) findViewById(R.id.iv_deal_banner)).setImageResource(bannerResId);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_favorite).setOnClickListener(this);
        findViewById(R.id.tv_hdr_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);
        findViewById(R.id.iv_deal_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);

        mActionBarHeight = Converter.convertDpToPixels(56);
        findViewById(R.id.scrollView).setOnTouchListener(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setHideOffset(0);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setShowHideAnimationEnabled(false);
        mActionBar.hide();
    }

    @Override
    public void onScrollChanged() {
        float y = (findViewById(R.id.scrollView)).getScrollY();
        if(y <= mActionBarHeight && mActionBar.isShowing()) {
            mActionBar.hide();
        } else if(!mActionBar.isShowing()) {
            mActionBar.show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionBar.show();
                break;
            case MotionEvent.ACTION_UP:
                mActionBar.hide();
        }
        return false;
    }
}