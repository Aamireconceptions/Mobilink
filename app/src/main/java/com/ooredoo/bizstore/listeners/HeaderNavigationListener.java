package com.ooredoo.bizstore.listeners;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyFavoritesActivity;
import com.ooredoo.bizstore.ui.activities.RecentSearchesActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.ui.activities.RedeemedDealsActivity;
import com.ooredoo.bizstore.ui.activities.ShareAppActivity;

/**
 * @author Pehlaj Rai
 * @since 6/23/2015.
 */
public class HeaderNavigationListener implements View.OnClickListener {

    View navHeaderView;
    HomeActivity mActivity;

    public HeaderNavigationListener(HomeActivity mActivity, View navHeaderView) {
        this.mActivity = mActivity;
        this.navHeaderView = navHeaderView;
        navHeaderView.findViewById(R.id.app_home).setOnClickListener(this);
        navHeaderView.findViewById(R.id.my_deals).setOnClickListener(this);
        navHeaderView.findViewById(R.id.recommended).setOnClickListener(this);
        navHeaderView.findViewById(R.id.recent_viewed).setOnClickListener(this);
        navHeaderView.findViewById(R.id.recent_searches).setOnClickListener(this);
        navHeaderView.findViewById(R.id.redeem_deals).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        mActivity.showHideDrawer(GravityCompat.START, false);
        switch(id) {
            case R.id.app_home:
                mActivity.hideSearchResults();
                mActivity.selectTab(0);
                break;
            case R.id.my_deals:
                //mActivity.startActivity(new Intent(mActivity, MyFavoritesActivity.class));
                break;
            case R.id.recommended:
                mActivity.startActivity(new Intent(mActivity, ShareAppActivity.class));
                break;
            case R.id.recent_searches:
               // mActivity.startActivity(new Intent(mActivity, RecentSearchesActivity.class));
                break;
            case R.id.recent_viewed:
                mActivity.startActivity(new Intent(mActivity, RecentViewedActivity.class));
                break;
            case R.id.redeem_deals:
                //mActivity.startActivity(new Intent(mActivity, RedeemedDealsActivity.class));
                break;
        }
    }

}
