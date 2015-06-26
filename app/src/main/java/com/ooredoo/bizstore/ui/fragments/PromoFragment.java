package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import static com.ooredoo.bizstore.AppConstant.DEAL;
import static com.ooredoo.bizstore.AppConstant.DEAL_CATEGORIES;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class PromoFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_promo, container, false);

        initAndLoadBanner(v);

        return v;
    }

    private void initAndLoadBanner(View v) {
        Bundle bundle = getArguments();

        int id = bundle.getInt("id");

        ImageView imageView = (ImageView) v.findViewById(R.id.image_view);
        imageView.setOnClickListener(this);

        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        MemoryCache memoryCache = MemoryCache.getInstance();

        String url = BaseAsyncTask.BASE_URL + id;

        Bitmap bitmap = memoryCache.getBitmapFromCache(url);

        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Logger.print("Root Width:" + v.getWidth());

            Resources resources = Resources.getSystem();

            int reqWidth = v.getWidth();
            int reqHeight = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._180sdp) / resources.getDisplayMetrics().density);

            Logger.print("req Width Pixels:" + reqWidth);
            Logger.print("req Height Pixels:" + reqHeight);

            BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(imageView, progressBar);
            bitmapDownloadTask.execute(url, String.valueOf(reqWidth), String.valueOf(reqHeight));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_view) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.showDetailActivity(DEAL, DEAL_CATEGORIES[0], 0L); //TODO replace 0L with deal id
        }
    }

    public static PromoFragment newInstance(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        PromoFragment fragment = new PromoFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

}