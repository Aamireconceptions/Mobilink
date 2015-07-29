package com.ooredoo.bizstore.listeners;

import android.view.View;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.fragments.EntertainmentFragment;
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
           id == R.id.fashion || id == R.id.restaurants)
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

                case R.id.fashion:

                    mActivity.selectTab(6);

                    break;

                case R.id.restaurants:

                    mActivity.selectTab(2);

                    break;
            }

            /*TopDealsFragment.subCategory = "top_deals_" + subCategory;
            mActivity.selectTab(1);*/
        }

        if(id == R.id.weight_loss || id == R.id.fitness || id == R.id.beauty_tips )
        {
            String subCategory = id == R.id.weight_loss ? "weight_loss" :
                    id == R.id.fitness ? "fitness" :
                                         "beauty_tips";

            SportsAndFitnessFragment.subCategory = "health_fitness_" + subCategory;
            mActivity.selectTab(11);
        }

        if(id == R.id.events || id == R.id.movie_tickets || id == R.id.jokes)
        {
            String subCategory = id == R.id.events ? "events" : id == R.id.movie_tickets ? "cinemas" : "kids_activities";
            EntertainmentFragment.subCategory = "entertainment_" + subCategory;
            mActivity.selectTab(9);
        }

        onFilterChangeListener.onFilterChange();
    }
}
