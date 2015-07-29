package com.ooredoo.bizstore.listeners;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;
import com.ooredoo.bizstore.ui.fragments.HotelsAndSpasFragment;
import com.ooredoo.bizstore.ui.fragments.SportsAndFitnessFragment;
import com.ooredoo.bizstore.ui.fragments.TopDealsFragment;

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

        if(id == R.id.entertainment || id == R.id.shopping ||
           id == R.id.electronics || id == R.id.restaurants)
        {
            /*String subCategory = id == R.id.entertainment ? "entertainment" :
                                   id == R.id.shopping ? "shopping" :
                                   id == R.id.fashion ? "fashion" :
                                                      "restaurants";*/
            switch (id)
            {
                case R.id.entertainment:

                    mActivity.selectTab(9);

                    break;

                case R.id.shopping:

                    mActivity.selectTab(3);

                    break;

                case R.id.electronics:

                    mActivity.selectTab(4);

                    break;

                case R.id.restaurants:

                    mActivity.selectTab(2);

                    break;
            }

            /*TopDealsFragment.subCategory = "top_deals_" + subCategory;
            mActivity.selectTab(1);*/
        }

        if(id == R.id.salons || id == R.id.lodging || id == R.id.spas )
        {
            String subCategory = id == R.id.salons ? "salons" :
                                 id == R.id.lodging ? "lodging" :
                                         "spas";

            HotelsAndSpasFragment.subCategory = "hotels_spas" + subCategory;
            mActivity.selectTab(5);
        }

        if(id == R.id.events || id == R.id.movie_tickets || id == R.id.kids_activities)
        {
            String subCategory = id == R.id.events ? "events" : id == R.id.movie_tickets ? "cinemas" : "kids_activities";
            EntertainmentFragment.subCategory = "entertainment_" + subCategory;
            mActivity.selectTab(9);
        }

        onFilterChangeListener.onFilterChange();
    }
}
