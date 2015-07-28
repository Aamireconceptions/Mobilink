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
import com.ooredoo.bizstore.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

public class EntertainmentFragment extends Fragment implements OnFilterChangeListener,
                                                               OnRefreshListener,
                                                               OnDealsTaskFinishedListener,
                                                               OnSubCategorySelectedListener
{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    public static String subCategory;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private boolean isCreated = false;

    public static EntertainmentFragment newInstance()
    {
        EntertainmentFragment fragment = new EntertainmentFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v);

        fetchAndDisplayEntertainment();

        isCreated = true;

        return v;
    }

    private void init(View v)
    {
        activity = (HomeActivity) getActivity();

        ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);

        CategoryUtils.showSubCategories(activity, CategoryUtils.CT_ENTERTAINMENT);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_ENTERTAINMENT);

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

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);
        adapter.setCategory(ResourceUtils.ENTERTAINMENT);

        TextView tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        ListView listView = (ListView) v.findViewById(R.id.list_view);
        listView.setEmptyView(tvEmptyView);
        listView.setOnScrollListener(new ScrollListener(activity));
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void fetchAndDisplayEntertainment()
    {
        DealsTask dealsTask = new DealsTask(activity, adapter, progressBar, ivBanner, this);

        if(isNotNullOrEmpty(subCategory)) {
            DealsTask.subCategories = subCategory;
            subCategory = ""; //Reset sub category filter.
        }

        dealsTask.execute("entertainment");
    }

    @Override
    public void onFilterChange()
    {
        fetchAndDisplayEntertainment();
    }

    @Override
    public void onRefreshStarted() {
        fetchAndDisplayEntertainment();
    }

    @Override
    public void onHaveDeals() {
        ivBanner.setImageResource(R.drawable.entertainment_banner);

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
        if(!isCreated)
        {
            onFilterChange();
        }
        else
        {
            isCreated = false;
        }
    }
}