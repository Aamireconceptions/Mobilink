package com.ooredoo.bizstore.listeners;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.AnimatorUtils;
import com.ooredoo.bizstore.utils.Logger;

/**
 * Created by Babar on 14-Jan-16.
 */
public class FabScrollListener implements AbsListView.OnScrollListener
{
    public HomeActivity activity;

    private static final int THRESHOLD = 5;

    public FabScrollListener(HomeActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private int lastPosition = -1;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if(lastPosition == firstVisibleItem)
        {
            return;
        }


        if(firstVisibleItem > lastPosition || firstVisibleItem < THRESHOLD)
        {
            Logger.print("Going Down");

            if(activity.fab.getVisibility() == View.VISIBLE)
            {
                activity.fab.setVisibility(View.GONE);


            }
        }
        else
        {
            Logger.print("Going Up");

            if(firstVisibleItem > THRESHOLD)
            {
                if(activity.fab.getVisibility() == View.GONE)
                {
                    activity.fab.setVisibility(View.VISIBLE);
                    AnimatorUtils.showFab(activity.fab);
                }
            }
        }

        lastPosition = firstVisibleItem;
    }
}
