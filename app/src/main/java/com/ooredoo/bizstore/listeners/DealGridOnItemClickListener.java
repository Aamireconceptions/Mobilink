package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
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
            genericDeal = (GenericDeal) parent.getItemAtPosition(position);

            //int dealId = gDeal.id;

            Intent intent = new Intent(activity, DealDetailActivity.class);
            intent.putExtra("generic_deal", genericDeal);
            //intent.putExtra(AppConstant.ID, dealId);
            intent.putExtra(CATEGORY, DEAL_CATEGORIES[0]); //TODO set proper deal category

            fragment.startActivityForResult(intent, 2);
        }
    }

    private boolean isSearchEnabled() {
        return activity instanceof HomeActivity && ((HomeActivity) activity).isSearchEnabled;
    }
}
