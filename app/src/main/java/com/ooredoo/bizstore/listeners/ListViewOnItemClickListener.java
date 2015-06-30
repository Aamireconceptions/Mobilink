package com.ooredoo.bizstore.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;

/**
 * Created by Babar on 29-Jun-15.
 */
public class ListViewOnItemClickListener implements AdapterView.OnItemClickListener
{
    private Context context;

    public ListViewOnItemClickListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        GenericDeal genericDeal = (GenericDeal) parent.getItemAtPosition(position);

        Intent intent = new Intent(context, DealDetailActivity.class);
        intent.putExtra("id", genericDeal.id);

        context.startActivity(intent);
    }
}
