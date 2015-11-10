package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.Business;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.BusinessDetailActivity;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.ui.fragments.ShoppingFragment;

import static com.ooredoo.bizstore.AppConstant.CATEGORY;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;

/**
 * @author Babar
 * @since 25-Jun-15.
 */
public class DealGridOnItemClickListener implements AdapterView.OnItemClickListener
{
    private Activity activity;

    private GridViewBaseAdapter adapter;

    public GenericDeal genericDeal;

    private ShoppingFragment fragment;
    public DealGridOnItemClickListener(Activity activity, GridViewBaseAdapter adapter, ShoppingFragment fragment)
    {
        this.activity = activity;

        this.adapter = adapter;

        this.fragment = fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isSearchEnabled()) {
            HomeActivity homeActivity = (HomeActivity) activity;
            homeActivity.showHideSearchBar(false);
        } else {

            if(adapter.listingType.equals("deals"))
            {
                genericDeal = (GenericDeal) parent.getItemAtPosition(position);

                //int dealId = gDeal.id;

                Deal recentDeal = new Deal((GenericDeal) parent.getItemAtPosition(position));
                RecentViewedActivity.addToRecentViewed(recentDeal);
                DealDetailActivity.selectedDeal = (GenericDeal) parent.getItemAtPosition(position);

                Intent intent = new Intent(activity, DealDetailActivity.class);
                intent.putExtra("generic_deal", genericDeal);
                //intent.putExtra(AppConstant.ID, dealId);
                intent.putExtra(CATEGORY, DEAL_CATEGORIES[0]); //TODO set proper deal category

                fragment.startActivityForResult(intent, 2);
            }
            else
            {
                Brand brand = (Brand) parent.getItemAtPosition(position);

                Business business = new Business(brand);

                Intent intent = new Intent();
                intent.setClass(activity, BusinessDetailActivity.class);
                intent.putExtra("business", business);
                intent.putExtra(CATEGORY, "");
                activity.startActivity(intent);
            }

        }
    }

    private boolean isSearchEnabled() {
        return activity instanceof HomeActivity && ((HomeActivity) activity).isSearchEnabled;
    }
}
