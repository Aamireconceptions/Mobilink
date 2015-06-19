package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.List;

/**
 * Created by Babar on 19-Jun-15.
 */
public class ListViewBaseAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    private List<GenericDeal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    public ListViewBaseAdapter(Context context, int layoutResId, List<GenericDeal> deals)
    {
        this.context = context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();
    }


    @Override
    public int getCount()
    {
        /*return deals.size();*/
        return 6;
    }

    @Override
    public GenericDeal getItem(int position)
    {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        /*return deals.get(position).id;*/
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        if(row == null)
        {
            row = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.tvCategory = (TextView) row.findViewById(R.id.category_icon);
            holder.ivFav = (ImageView) row.findViewById(R.id.fav);
            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            holder.tvDescription = (TextView) row.findViewById(R.id.description);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
            holder.tvViews = (TextView) row.findViewById(R.id.views);
            holder.rbRatings = (RatingBar) row.findViewById(R.id.ratings);

            row.setTag(holder);
        }
        else
        {
            holder = (Holder) row.getTag();
        }

        return row;
    }

    private static class Holder
    {
        ImageView ivFav;

        TextView tvCategory, tvTitle, tvDescription, tvDiscount, tvViews;

        RatingBar rbRatings;
    }
}
