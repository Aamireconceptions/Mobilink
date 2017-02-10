package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Gallery;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapForceDownloadTask;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.utils.CommonHelper;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.FragmentUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author Babar
 * @since 19-Jun-15.
 */
public class GalleryFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private DealDetailActivity activity;

    private int id;

    private Bitmap bitmap;

    private Gallery gallery;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    public static GalleryFragment newInstance(Gallery gallery, int position, boolean clickable)
    {
        Bundle bundle = new Bundle();
        /*bundle.putInt("id", id);
        bundle.putString("image_url", imgUrl);*/
        bundle.putSerializable("gallery", gallery);
        bundle.putInt("pos", position);
        bundle.putBoolean("clickable", clickable);

        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        v.setOnClickListener(this);

        initAndLoadTopBrand(v);

        return v;
    }

    ImageView imageView;
    ProgressBar progressBar;
    int pos;

    PhotoViewAttacher photoViewAttacher;
    private void initAndLoadTopBrand(View v)
    {
        Resources resources = activity.getResources();

        final int reqWidth = resources.getDisplayMetrics().widthPixels ;

        final int reqHeight = resources.getDisplayMetrics().heightPixels;

        pos = getArguments().getInt("pos");

        activity = (DealDetailActivity) getActivity();

        Bundle bundle = getArguments();

        gallery = (Gallery) bundle.getSerializable("gallery");

        String imgUrl = gallery.image;

       // id = brand.id;

        imageView = (ImageView) v.findViewById(R.id.image_view);



        if(!getArguments().getBoolean("clickable"))
        {
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rlImage = (RelativeLayout) v.findViewById(R.id.image_layout);
            rlImage.setLayoutParams(params);

            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackground(null);
           // imageView.setOnTouchListener(this);

            photoViewAttacher = new PhotoViewAttacher(imageView);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        if(imgUrl != null)
        {
            Logger.print("Top Brand imgUrl was NOT null");

            String url = BaseAsyncTask.IMAGE_BASE_URL + imgUrl;

            Logger.logE("FRAGMENT URL:", url);

            bitmap = memoryCache.getBitmapFromCache(url);

            if(bitmap == null)
            {
                new CommonHelper().fallBackToDiskCache(getActivity(), url, diskCache, memoryCache,
                        imageView, progressBar, reqWidth, reqHeight);
            }
            else
            {
                imageView.setImageBitmap(bitmap);
            }
        }
        else
        {
            Logger.print("Top Brand imgUrl was null");
        }
    }

    /*private void fallBackToDiskCache(final String url)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                bitmap = diskCache.getBitmapFromDiskCache(url);

                Logger.print("dCache getting bitmap from cache");

                if(bitmap != null)
                {
                    Logger.print("dCache found!");

                    memoryCache.addBitmapToCache(url, bitmap);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Logger.print("Setting bitmap: "+bitmap);
                            imageView.setImageBitmap(bitmap);
                            imageView.setTag("loaded");
                        }
                    });
                }
                else
                {
                    Resources resources = activity.getResources();

                    final int reqWidth = resources.getDisplayMetrics().widthPixels ;

                    final int reqHeight = resources.getDisplayMetrics().heightPixels;

                    Logger.print("req Width Pixels:" + reqWidth);
                    Logger.print("req Height Pixels:" + reqHeight);

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            BitmapForceDownloadTask bitmapDownloadTask =
                                    new BitmapForceDownloadTask(imageView, progressBar);
                            bitmapDownloadTask.executeOnExecutor
                                    (AsyncTask.THREAD_POOL_EXECUTOR, url,
                                            String.valueOf(reqWidth), String.valueOf(reqHeight));
                        }
                    });
                }
            }
        });

        thread.start();
    }*/

    @Override
    public void onClick(View v)
    {
        if(getArguments().getBoolean("clickable") && ((imageView.getTag() != null
                && imageView.getTag().equals("loaded")) || bitmap != null)) {

            Logger.print("CLICKED");
            FragmentUtils.addFragmentWithBackStack(activity, android.R.id.content,
                    ImageViewerFragment.newInstance(activity.galleryList, pos), "gallery");

        }
    }

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix saveMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();

    float oldDist = 1f;

    private static final String TAG = "Touch";

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        ImageView imageView = (ImageView) view;

        // make the image scalable as a matrix
        imageView.setScaleType(ImageView.ScaleType.MATRIX);

        float scale;
        int strange = event.getAction() & MotionEvent.ACTION_MASK;
        Logger.print("Strange: "+strange);

        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                saveMatrix.set(matrix);
                start.set(event.getX(), event.getY());

                Log.d(TAG, "mode=DRAG");

                mode = DRAG;

                break;

            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = NONE;

                Log.d(TAG, "mode=NONE" );

                break;

            case MotionEvent.ACTION_POINTER_DOWN: //second finger down

                oldDist = (float) spacing(event); // calculates the distance between two points where user touched.
                Log.d(TAG, "oldDist=" + oldDist);

                // minimal distance between both the fingers
                if(oldDist > 5f)
                {
                    saveMatrix.set(matrix);
                    midPoint(mid, event); // sets the mid-point of the straight line between two points where user touched.
                    mode = ZOOM;

                    Log.d(TAG, "mode=ZOOM" );
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if(mode == DRAG)
                {
                    //movement of first finger
                    matrix.set(saveMatrix);
                   /* if(view.getLeft() >= -392)
                    {
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }*/


                }
                else
                    if(mode == ZOOM)
                    {
                        float newDist = (float) spacing(event);
                        Log.d(TAG, "newDist=" + newDist);
                        if(newDist > 5f)
                        {
                            matrix.set(saveMatrix);
                            scale = newDist / oldDist; //thinking I need to play around with this value to limit it**
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }

                break;
        }

        // Perform the transformation
        imageView.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private double spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);

        point.set(x / 2, y / 2);
    }
}