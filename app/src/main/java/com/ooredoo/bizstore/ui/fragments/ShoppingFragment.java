package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.adapters.SimilarBrandsAdapter;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.interfaces.ScrollToTop;
import com.ooredoo.bizstore.listeners.DealGridOnItemClickListener;
import com.ooredoo.bizstore.listeners.FabScrollListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Image;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.SnackBarUtils;
import com.ooredoo.bizstore.views.HeaderGridView;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.CategoryUtils.CT_SHOPPING;
import static com.ooredoo.bizstore.utils.CategoryUtils.showSubCategories;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.clearCache;

/**
 * @author Babar
 */
public class ShoppingFragment extends Fragment implements OnFilterChangeListener,
                                                          OnDealsTaskFinishedListener,
                                                          OnSubCategorySelectedListener,
                                                          SwipeRefreshLayout.OnRefreshListener,
        ScrollToTop{
    private HomeActivity activity;

    private List<GenericDeal> deals;

    private GridViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private ImageView ivBanner;

    private HeaderGridView gridView;

    private boolean isCreated = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    private DealGridOnItemClickListener dealGridOnItemClickListener;

    private boolean isRefreshed = false;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    private View layoutFilterTags;

    private TextView tvFilter;

    private ImageView ivClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping, container, false);

        init(v, inflater);

        isCreated = true;

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(View v, LayoutInflater inflater) {
        activity = (HomeActivity) getActivity();

        snackBarUtils = new SnackBarUtils(activity, v);

        deals = new ArrayList<>();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.shopping_gridview);
        swipeRefreshLayout.setOnRefreshListener(this);

       // rlHeader = (RelativeLayout) v.findViewById(R.id.header);

        //rlHeader = (RelativeLayout) inflater.inflate(R.layout.layout_filter_header, null);

        //ivBanner = (ImageView) inflater.inflate(R.layout.image_view, null);

        adapter = new GridViewBaseAdapter(activity, R.layout.grid_generic, deals);
        adapter.setListingType("deals");

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        gridView = (HeaderGridView) v.findViewById(R.id.shopping_gridview);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            if(BizStore.getLanguage().equals("ar"))
                gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._minus3sdp));
           /* else
                gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._6sdp));*/
        }

        //gridView.addHeaderView(ivBanner);
        //gridView.addHeaderView(rlHeader);

        //gridView.setEmptyView(tvEmptyView);
        dealGridOnItemClickListener = new DealGridOnItemClickListener(activity, adapter, this);
        gridView.setOnItemClickListener(dealGridOnItemClickListener);
        gridView.setAdapter(adapter);
        gridView.setOnScrollListener(new FabScrollListener(activity));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            gridView.setNestedScrollingEnabled(true);
            gridView.setDrawSelectorOnTop(true);
        }
        else
        {
            gridView.setSelector(new ColorDrawable());
        }

        layoutFilterTags = v.findViewById(R.id.filter_tags);

        tvFilter = (TextView) layoutFilterTags.findViewById(R.id.filter);
        FontUtils.setFont(activity, tvFilter);

        ivClose = (ImageView) layoutFilterTags.findViewById(R.id.close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutFilterTags.setVisibility(View.GONE);
            }
        });

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CT_SHOPPING);
        clickListener.setLayout(v);

        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.VISIBLE);

        showSubCategories(activity, CT_SHOPPING);

        loadDeals(progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.print("Testing: ShoppingOnResume");
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            if(BizStore.getLanguage().equals("ar"))
                gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._minus1sdp));
            else
                gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._8sdp));
        }
    }*/

    ShoppingTask shoppingTask;

    private void loadDeals(ProgressBar progressBar)
    {
        tvEmptyView.setVisibility(View.GONE);

        shoppingTask = new ShoppingTask(activity, adapter, progressBar, snackBarUtils, this);

        String cache = shoppingTask.getCache("shopping");

        if(cache != null && !isRefreshed)
        {
            shoppingTask.setData(cache);
        }
        else
        {
            shoppingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "shopping");
        }
    }

    public static ShoppingFragment newInstance() {
        ShoppingFragment fragment = new ShoppingFragment();

        return fragment;
    }

    @Override
    public void onFilterChange() {

        isRefreshed = true;

        if(brandsAdapter!= null)
        {
            brandsAdapter.clearData();
            brandsAdapter.notifyDataSetChanged();
        }
        adapter.clearData();

        adapter.notifyDataSetChanged();
        tvEmptyView.setText("");

        shoppingTask.cancel(true);

        if(ShoppingTask.sortColumn.equals("createdate"))
        {
            gridView.setAdapter(adapter);
            gridView.setNumColumns(2);
            adapter.setListingType("deals");

            /*gridView.setHorizontalSpacing((int) getResources().getDimension(R.dimen._6sdp));
            gridView.setVerticalSpacing((int) getResources().getDimension(R.dimen._6sdp));*/

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                if(BizStore.getLanguage().equals("ar"))
                    gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._minus3sdp));
/*                else
                    gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._6sdp));*/
            }
        }
        else
        {
            gridView.setNumColumns(3);

            adapter.setListingType("brands");

          /*  gridView.setHorizontalSpacing((int) getResources().getDimension(R.dimen._22sdp));
            gridView.setVerticalSpacing((int) getResources().getDimension(R.dimen._6sdp));*/

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                if(BizStore.getLanguage().equals("ar"))
                    gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._minus1sdp));
   /*             else
                    gridView.setHorizontalSpacing((int) activity.getResources().getDimension(R.dimen._8sdp));*/
            }
        }

        loadDeals(progressBar);

        isRefreshed = false;
    }

    SimilarBrandsAdapter brandsAdapter;
    public void showBrands(List<Brand> brands)
    {
        brandsAdapter = new SimilarBrandsAdapter(activity, R.layout.grid_brand, brands);
        gridView.setAdapter(brandsAdapter);
    }

    @Override
    public void onRefresh() {
       layoutFilterTags.setVisibility(View.GONE);

        tvEmptyView.setText("");
        memoryCache.remove(adapter.deals);

        diskCache.remove(adapter.deals);

        final String KEY = PREFIX_DEALS.concat("shopping");
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        clearCache(activity, KEY);
        clearCache(activity, UPDATE_KEY);
        activity.resetFilters();

        ShoppingTask.subCategories = null;

        CategoryUtils.showSubCategories(activity, CategoryUtils.CT_SHOPPING);

        isRefreshed = true;
        loadDeals(null);
        isRefreshed = false;
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals()
    {
        //rlHeader.setVisibility(View.VISIBLE);

        //ivBanner.setImageResource(R.drawable.shopping_banner);

        tvEmptyView.setText("");
    }

    @Override
    public void onNoDeals(int stringResId) {
       // rlHeader.setVisibility(View.GONE);

      //  ivBanner.setImageBitmap(null);
        //ivBanner.setImageResource(R.drawable.shopping_banner);

        tvEmptyView.setVisibility(View.VISIBLE);
        tvEmptyView.setText(stringResId);
        //gridView.setEmptyView(tvEmptyView);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCompatActivity.RESULT_OK)
        {
            boolean isFav = data.getBooleanExtra("is_fav", false);

            dealGridOnItemClickListener.genericDeal.isFav = isFav;

            String voucher = data.getStringExtra("voucher");

            if(voucher != null)
            {
                dealGridOnItemClickListener.genericDeal.voucher = voucher;
                dealGridOnItemClickListener.genericDeal.status = "Available";
            }

            int views = data.getIntExtra("views", -1);

            dealGridOnItemClickListener.genericDeal.views = views;

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void scroll() {
        gridView.setSelection(0);
    }

    @Override
    public void filterTagUpdate() {
        String filter = "";

        if(activity.doApplyDiscount)
        {
            filter = "Discount: Highest to lowest, ";
        }

        if(activity.doApplyRating)
        {
            filter += "Rating " + activity.ratingFilter +", ";
        }

        String categories = CategoryUtils.getSelectedSubCategoriesForTag(CategoryUtils.CT_SHOPPING);

        if(!categories.isEmpty())
        {
            filter += "Sub Categories: "+categories ;
        }

        if(! filter.isEmpty() && filter.charAt(filter.length() - 2) == ',')
        {
            filter = filter.substring(0, filter.length() - 2);
        }

        if(!filter.isEmpty())
        {
            layoutFilterTags.setVisibility(View.VISIBLE);

            FontUtils.changeColorAndMakeBold(tvFilter,
                    activity.getString(R.string.filter) + " : " + filter,
                    activity.getString(R.string.filter) + " : ", activity.getResources().getColor(R.color.black));
        }
        else
        {
            layoutFilterTags.setVisibility(View.GONE);
        }
    }
}