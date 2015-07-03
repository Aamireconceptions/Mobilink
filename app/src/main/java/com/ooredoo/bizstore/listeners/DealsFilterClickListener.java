package com.ooredoo.bizstore.listeners;

import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.Button;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author Pehlaj Rai
 * @since 6/19/2015.
 */
public class DealsFilterClickListener implements View.OnClickListener
{

    View dealsFilterView;
    HomeActivity mActivity;

    Button btnNewDeals, btnPopularDeals;

    public DealsFilterClickListener(HomeActivity mActivity, View dealsFilterView) {
        this.mActivity = mActivity;
        this.dealsFilterView = dealsFilterView;

        /*btnNewDeals = (Button) dealsFilterView.findViewById(R.id.btn_new_deals);
        btnPopularDeals = (Button) dealsFilterView.findViewById(R.id.btn_popular_deals);*/

        btnNewDeals.setSelected(true);
        btnNewDeals.setOnClickListener(this);
        btnPopularDeals.setOnClickListener(this);
        dealsFilterView.findViewById(R.id.iv_filter).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
       /* if(id == R.id.btn_new_deals || id == R.id.btn_popular_deals) {
            boolean isNewDealsFilter = id == R.id.btn_new_deals;
            btnNewDeals.setSelected(isNewDealsFilter);
            btnPopularDeals.setSelected(!isNewDealsFilter);
            //TODO implement deals' filter
        } else if(id == R.id.iv_filter) {
            mActivity.showHideDrawer(GravityCompat.END, true);
        }*/
    }
}
