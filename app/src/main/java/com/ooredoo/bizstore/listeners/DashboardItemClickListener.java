package com.ooredoo.bizstore.listeners;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;

/**
 * @author Pehlaj Rai
 * @since 6/19/2015.
 */
public class DashboardItemClickListener implements View.OnClickListener {

    HomeActivity mActivity;

    public DashboardItemClickListener(HomeActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.events || id == R.id.movie_tickets || id == R.id.jokes) {
            String subCategory = id == R.id.events ? "events" : id == R.id.movie_tickets ? "cinemas" : "kids_activities";
            EntertainmentFragment.subCategory = "entertainment_" + subCategory;
            mActivity.selectTab(9);
        }
    }
}
