package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.utils.AnimUtils;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.List;

import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class RedeemedDealsBaseAdapter extends BaseAdapter {
    private Context context;

    private int layoutResId;

    private List<GenericDeal> deals;

    private LayoutInflater inflater;

    private Holder holder;

    private int prevItem = -1;

    public RedeemedDealsBaseAdapter(Context context, int layoutResId, List<GenericDeal> deals) {
        this.context = context;

        this.layoutResId = layoutResId;

        this.deals = deals;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public GenericDeal getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return deals.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GenericDeal deal = getItem(position);

        View row = convertView;

        if(row == null) {
            row = inflater.inflate(layoutResId, parent, false);

            holder = new Holder();

            holder.tvCategory = (TextView) row.findViewById(R.id.category_icon);
            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            holder.tvDetail = (TextView) row.findViewById(R.id.detail);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
            holder.tvValidity = (TextView) row.findViewById(R.id.validity);
            holder.tvCode = (TextView) row.findViewById(R.id.code);
            holder.tvRedeemedOn = (TextView) row.findViewById(R.id.redeemed_on);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        String category = deal.category;
        holder.tvCategory.setText(category);

        int categoryDrawable = ResourceUtils.getDrawableResId(category);
        if(categoryDrawable > 0) {
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(categoryDrawable, 0, 0, 0);
        }

        holder.tvTitle.setText(deal.title);

        holder.tvDetail.setText(deal.description);

        holder.tvDiscount.setText(valueOf(deal.discount) + context.getString(R.string.percentage_off));

        holder.tvValidity.setText("Redeem Discount Until " + deal.endDate);

        holder.tvCode.setText(deal.voucher);

        holder.tvRedeemedOn.setText(deal.startDate);

        AnimUtils.slideView((Activity) context, row, prevItem < position);

        prevItem = position;

        return row;
    }


    private static class Holder {

        TextView tvCategory, tvTitle, tvDetail, tvDiscount, tvValidity, tvCode, tvRedeemedOn;
    }
}