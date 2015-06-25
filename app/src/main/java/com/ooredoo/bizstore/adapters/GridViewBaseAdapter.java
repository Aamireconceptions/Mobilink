package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.MemoryCache;

import java.util.List;

/**
 * @author Babar
 * @since 18-Jun-15
 */
public class GridViewBaseAdapter extends BaseAdapter
{
    private Context context;

    private int layoutResId;

    private List<GenericDeal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private MemoryCache memoryCache;

    public GridViewBaseAdapter(Context context, int layoutResId, List<GenericDeal> deals)
    {
        this.context = context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        memoryCache = MemoryCache.getInstance();
    }

    public void setData(List<GenericDeal> deals)
    {
        this.deals = deals;
    }



    @Override
    public int getCount()
    {
       /* return deals.size();*/
        return 8;
    }

    @Override
    public GenericDeal getItem(int position)
    {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position)
    {
       /* return deals.get(position).id;*/
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View grid = convertView;

        if(grid == null)
        {
            grid = inflater.inflate(layoutResId, parent, false);

           holder = new Holder();

            holder.ivThumbnail = (ImageView) grid.findViewById(R.id.thumbnail);
            holder.ivFav = (ImageView) grid.findViewById(R.id.fav);
            holder.tvTitle = (TextView) grid.findViewById(R.id.title);
            holder.tvDiscount = (TextView) grid.findViewById(R.id.discount);

            grid.setTag(holder);
        }
        else
        {
            holder = (Holder) grid.getTag();
        }

        holder.ivFav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GenericDeal genericDeal = getItem(position);
                genericDeal.isFav = !v.isSelected();

                notifyDataSetChanged();
            }
        });

       /* holder.ivFav.setSelected(getItem(position).isFav);

        holder.tvTitle.setText(getItem(position).title);
        holder.tvDiscount.setText(getItem(position).discount);*/

        return grid;
    }

    private static class Holder
    {
        ImageView ivThumbnail, ivFav;

        TextView tvTitle, tvDiscount;
    }
}