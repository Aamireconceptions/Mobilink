package com.ooredoo.bizstore.listeners;

import android.os.Build;
import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author Pehlaj Rai
 * @since 6/19/2015.
 */
public class DashboardItemClickListener implements View.OnClickListener {

    HomeActivity mActivity;

    OnFilterChangeListener onFilterChangeListener;

    public DashboardItemClickListener(HomeActivity mActivity) {
        this.mActivity = mActivity;

        onFilterChangeListener = mActivity;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(mActivity.isSearchEnabled) {
            mActivity.showHideSearchBar(false);
        } else {

            if(id == R.id.restaurants_layout || id == R.id.shopping_layout ||
                    id == R.id.entertainment_layout ) {

                switch(id) {
                    case R.id.restaurants_layout:

                        mActivity.selectTab(2);

                        break;

                    case R.id.shopping_layout:

                        mActivity.selectTab(3);

                        break;

                    case R.id.entertainment_layout:

                        mActivity.selectTab(5);

                        break;
                }
            }

            if(id == R.id.search_layout)
            {
                mActivity.clickSearch();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    mActivity.resetToolBarPosition();
                }

                return;
            }

            if(id == R.id.nearby_layout)
            {
                mActivity.selectTab(1);
                return;
            }
        }
    }


}
