package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnRefreshListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.listeners.ScrollListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class FoodAndDiningFragment extends Fragment implements OnFilterChangeListener,
                                                               OnRefreshListener,
                                                               OnDealsTaskFinishedListener,
                                                               OnSubCategorySelectedListener
{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private boolean isRecreated = false;

    public static FoodAndDiningFragment newInstance() {
        FoodAndDiningFragment fragment = new FoodAndDiningFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v);

        fetchAndDisplayFoodAndDining();

        isRecreated = true;

        return v;
    }

    private void init(View v)
    {
        activity = (HomeActivity) getActivity();

        ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_FOOD);

        Button btNewDeals = (Button) v.findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(clickListener);
        clickListener.setButtonSelected(btNewDeals);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btNewDeals);

        Button btPopularDeals = (Button) v.findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btPopularDeals);

        ImageView ivFilter = (ImageView) v.findViewById(R.id.filter);
        ivFilter.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals);
        adapter.setCategory(ResourceUtils.FOOD_AND_DINING);

        TextView tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        ListView listView = (ListView) v.findViewById(R.id.list_view);
        listView.setEmptyView(tvEmptyView);
        listView.setOnScrollListener(new ScrollListener(activity));
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void fetchAndDisplayFoodAndDining() {
        DealsTask dealsTask = new DealsTask(activity, adapter,
                                            progressBar, ivBanner,
                                            this);
        dealsTask.execute("food");
    }

    @Override
    public void onFilterChange()
    {
        Logger.print("FoodAndDiningFragment onFilterChange");

        fetchAndDisplayFoodAndDining();
    }

    @Override
    public void onRefreshStarted()
    {
        fetchAndDisplayFoodAndDining();
    }

    @Override
    public void onHaveDeals()
    {
        ivBanner.setImageResource(R.drawable.food_dinning_banner);

        rlHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals() {
        ivBanner.setImageDrawable(null);

        rlHeader.setVisibility(View.GONE);
    }

    @Override
    public void onSubCategorySelected()
    {
        if(isRecreated)
        {
            onFilterChange();
        }
    }
}