package com.ooredoo.bizstore.ui.fragments;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Home;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.RecentViewedActivity;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import static com.ooredoo.bizstore.AppConstant.DEAL;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class PromoFragment extends Fragment implements View.OnClickListener
{
    private HomeActivity activity;

    private int id;

    private Bitmap bitmap;

    MemoryCache memoryCache = MemoryCache.getInstance();

    DiskCache diskCache = DiskCache.getInstance();

    private GenericDeal genericDeal;

    private ImageView imageView;

    private ProgressBar progressBar;

    private View v;

    public static PromoFragment newInstance(GenericDeal genericDeal)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("generic_deal", genericDeal);

        PromoFragment fragment = new PromoFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_promo, container, false);
        v.setOnClickListener(this);

        initAndLoadBanner(v);

        return v;
    }

    private void initAndLoadBanner(View v)
    {
        activity = (HomeActivity) getActivity();

        Bundle bundle = getArguments();

        genericDeal = (GenericDeal) bundle.getSerializable("generic_deal");

        String imgUrl = genericDeal.image.promotionalUrl;

        id = bundle.getInt("id");

        imageView = (ImageView) v.findViewById(R.id.image_view);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        Resources resources = activity.getResources();

        final int reqWidth = resources.getDisplayMetrics().widthPixels;

        final int reqHeight =  (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._190sdp));

        if(imgUrl != null)
        {
            Logger.print("imgUrl was NOT null");

            String url = BaseAsyncTask.IMAGE_BASE_URL + imgUrl;

            Logger.logE("FRAGMENT URL:", url);

            bitmap = memoryCache.getBitmapFromCache(url);

            if(bitmap == null)
            {
                new CommonHelper().fallBackToDiskCache(getActivity(), url, diskCache, memoryCache, imageView,
                        progressBar, reqWidth, reqHeight);
            }
            else
            {
                imageView.setImageBitmap(bitmap);
            }

            Logger.print("dCache PromoFragment bitmap: " +bitmap);
        }
        else
        {
            Logger.print("imgUrl was null");
        }
    }

    @Override
    public void onClick(View v)
    {
        genericDeal.views = ++genericDeal.views;

        Deal recentDeal = new Deal(genericDeal);
        RecentViewedActivity.addToRecentViewed(recentDeal);

        activity.showDealDetailActivity(DEAL_CATEGORIES[0], genericDeal );
    }
}