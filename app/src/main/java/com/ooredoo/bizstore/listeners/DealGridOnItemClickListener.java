package com.ooredoo.bizstore.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;

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

    public DealGridOnItemClickListener(Activity activity, GridViewBaseAdapter adapter)
    {
        this.activity = activity;

        this.adapter = adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        GenericDeal genericDeal = adapter.getItem(position);

        int dealId = genericDeal.id;

        Intent intent = new Intent(activity, DealDetailActivity.class);
        intent.putExtra(AppConstant.ID, dealId);
        intent.putExtra(CATEGORY, DEAL_CATEGORIES[0]); //TODO set proper deal category

        activity.startActivity(intent);
    }
}
