package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author Pehlaj Rai
 * @since 19-Jun-15.
 */
public class DealDetailFragment extends Fragment implements View.OnClickListener, ViewTreeObserver.OnScrollChangedListener {
    public boolean showBanner = false;
    public boolean isFavorite = false;
    public int bannerResId = R.drawable.tmp_banner;
    private float mActionBarHeight;
    private ActionBar mActionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deal_details, container, false);
        ((ImageView) v.findViewById(R.id.iv_deal_banner)).setImageResource(bannerResId);
        v.findViewById(R.id.iv_back).setOnClickListener(this);
        v.findViewById(R.id.iv_favorite).setOnClickListener(this);
        v.findViewById(R.id.tv_hdr_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.iv_deal_banner).setVisibility(showBanner ? View.VISIBLE : View.GONE);

        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
        mActionBarHeight = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        mActionBar = ((HomeActivity) getActivity()).mActionBar;
        v.findViewById(R.id.scrollView).getViewTreeObserver().addOnScrollChangedListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_back) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.showTabs();
            if(!showBanner) {
                homeActivity.showHideSearchBar(true);
            }
        } else if(viewId == R.id.iv_favorite) {
            isFavorite = !isFavorite;
            int favDrawable = isFavorite ? R.drawable.ic_like_big_active : R.drawable.ic_like_big_inactive;
            ((ImageView) getView().findViewById(R.id.iv_favorite)).setImageResource(favDrawable);
        }
    }

    @Override
    public void onScrollChanged() {
        float y = (getView().findViewById(R.id.scrollView)).getScrollY();
        if(y >= mActionBarHeight && mActionBar.isShowing()) {
            mActionBar.hide();
        } else if(y == 0 && !mActionBar.isShowing()) {
            mActionBar.show();
        }
    }

}