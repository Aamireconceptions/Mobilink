package com.ooredoo.bizstore.listeners;

import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.Button;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.ColorUtils;

/**
 * @author Pehlaj Rai
 * @since 6/19/2015.
 */
public class DealsFilterClickListener implements View.OnClickListener {

    View dealsFilterView;
    HomeActivity mActivity;

    Button btnNewDeals, btnPopularDeals;

    public DealsFilterClickListener(HomeActivity mActivity, View dealsFilterView) {
        this.mActivity = mActivity;
        this.dealsFilterView = dealsFilterView;
        btnNewDeals = (Button) dealsFilterView.findViewById(R.id.btn_new_deals);
        btnPopularDeals = (Button) dealsFilterView.findViewById(R.id.btn_popular_deals);
        btnNewDeals.setOnClickListener(this);
        btnPopularDeals.setOnClickListener(this);
        dealsFilterView.findViewById(R.id.ib_filter).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_new_deals || id == R.id.btn_popular_deals) {
            btnNewDeals.setTextColor(id == R.id.btn_new_deals ? ColorUtils.WHITE : ColorUtils.BLACK);
            btnPopularDeals.setTextColor(id == R.id.btn_new_deals ? ColorUtils.BLACK : ColorUtils.WHITE);
            btnNewDeals.setBackgroundResource(id == R.id.btn_new_deals ? R.drawable.btn_red1 : R.drawable.btn_lt_grey1);
            btnPopularDeals.setBackgroundResource(id == R.id.btn_new_deals ? R.drawable.btn_lt_grey2 : R.drawable.btn_red2);
        } else if(id == R.id.ib_filter) {
            mActivity.showHideDrawer(GravityCompat.END, true);
        }
    }
}
