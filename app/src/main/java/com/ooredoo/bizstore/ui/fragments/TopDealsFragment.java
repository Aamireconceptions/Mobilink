package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TopDealsFragment extends Fragment implements OnFilterChangeListener,
                                                          OnDealsTaskFinishedListener,
                                                          OnSubCategorySelectedListener,
                                                          SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private ListView listView;

    private boolean isCreated = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    public static TopDealsFragment newInstance() {
        TopDealsFragment fragment = new TopDealsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v, inflater);

        loadTopDeals(progressBar);

        isCreated = true;

        return v;
    }

    private void init(View v, LayoutInflater inflater)
    {
        activity = (HomeActivity) getActivity();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.list_view, R.id.empty_view, R.id.appBarLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

       /* ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);*/

         ivBanner = (ImageView) inflater.inflate(R.layout.image_view, null);

        rlHeader = (RelativeLayout) inflater.inflate(R.layout.layout_filter_header, null);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_TOP);
        clickListener.setLayout(rlHeader);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals);
        adapter.setCategory(ResourceUtils.TOP_DEALS);

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        listView = (ListView) v.findViewById(R.id.list_view);
        listView.addHeaderView(ivBanner);
        listView.addHeaderView(rlHeader);
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void loadTopDeals(ProgressBar progressBar)
    {
        DealsTask dealsTask = new DealsTask(activity, adapter, progressBar, ivBanner, this);
        dealsTask.execute("top_deals");
    }

    @Override
    public void onFilterChange() {
        loadTopDeals(progressBar);
    }

    @Override
    public void onRefresh() {
        loadTopDeals(null);
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {
        ivBanner.setImageResource(R.drawable.top_deals_banner);

        rlHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals(int stringResId) {
        ivBanner.setImageDrawable(null);

        rlHeader.setVisibility(View.GONE);

        tvEmptyView.setText(stringResId);
        listView.setEmptyView(tvEmptyView);
    }

    @Override
    public void onSubCategorySelected() {
        if(!isCreated) {
            onFilterChange();
        } else {
            isCreated = false;
        }
    }
}