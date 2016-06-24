package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.AnimUtils;
import com.ooredoo.bizstore.utils.FontUtils;
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

    public boolean available = false;

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

            holder.tvTitle = (TextView) row.findViewById(R.id.title);
            FontUtils.setFontWithStyle(context, holder.tvTitle, Typeface.BOLD);
            holder.tvDetail = (TextView) row.findViewById(R.id.detail);
            holder.tvDiscount = (TextView) row.findViewById(R.id.discount);
            FontUtils.setFontWithStyle(context, holder.tvDiscount, Typeface.BOLD);
            holder.tvValidity = (TextView) row.findViewById(R.id.validity);
            holder.tvCode = (TextView) row.findViewById(R.id.code);
            holder.tvRedeemedOn = (TextView) row.findViewById(R.id.redeemed_on);
            holder.llRedeemOn = (LinearLayout) row.findViewById(R.id.redeem_on_layout);


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                row.findViewById(R.id.layout_deal_detail)
                        .setBackgroundResource(R.drawable.redeemed_ripple);
            }

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        row.findViewById(R.id.layout_deal_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(deal);
            }
        });


        holder.tvTitle.setText(deal.title);

        holder.tvDetail.setText(deal.description);

        holder.tvDiscount.setText(valueOf(deal.discount) + context.getString(R.string.percentage_off));

        holder.tvValidity.setText(context.getString(R.string.redeem_until) + " " + deal.endDate);

        if(available)
        {
            holder.llRedeemOn.setVisibility(View.GONE);
        }
        else
        {
            holder.tvRedeemedOn.setText(deal.startDate);
            holder.llRedeemOn.setVisibility(View.VISIBLE);
        }


        return row;
    }

    private void showDetail(GenericDeal deal) {
        Deal recentDeal = new Deal(deal);
        RecentViewedActivity.addToRecentViewed(recentDeal);
        DealDetailActivity.selectedDeal = deal;
        Intent intent = new Intent();
        intent.setClass(context, DealDetailActivity.class);
        intent.putExtra("generic_deal", deal);
        context.startActivity(intent);
    }

    private static class Holder {

        TextView tvTitle, tvDetail, tvDiscount, tvValidity, tvCode, tvRedeemedOn,
                tvTimeStamp, tvDiscountAvailed;

        LinearLayout llRedeemOn;
    }
}