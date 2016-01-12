package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.GenericDeal;

import java.util.List;

/**
 * Created by Babar on 12-Jan-16.
 */
public class DealOfDayAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    private List<GenericDeal> deals;

    private LayoutInflater layoutInflater;

    private Holder holder;

    public DealOfDayAdapter(Context context, int layoutResId, List<GenericDeal> deals)
    {
        this.context  = context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public Object getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid = convertView;

        if(grid == null)
        {
            grid = layoutInflater.inflate(layoutResId, parent, false);

            holder = new Holder();
            holder.tvCategory = (TextView) grid.findViewById(R.id.category);

            holder.gridLayout = (GridLayout) grid.findViewById(R.id.grid);

        }

        return grid;
    }

    public static class Holder
    {
        TextView tvCategory;

        GridLayout gridLayout;
    }
}
