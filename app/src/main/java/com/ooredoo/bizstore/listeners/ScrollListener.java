package com.ooredoo.bizstore.listeners;

import android.widget.AbsListView;
import android.widget.ListView;

import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

/**
 * Created by Babar on 22-Jul-15.
 */
public class ScrollListener implements AbsListView.OnScrollListener
{
    HomeActivity homeActivity;

    public ScrollListener(HomeActivity homeActivity)
    {
        this.homeActivity = homeActivity;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        int topRowVerticalPosition = (view == null || view.getChildCount() == 0)
                                      ? 0
                                      : view.getChildAt(0).getTop();

        homeActivity.setSwipeRefreshLayoutEnabled(topRowVerticalPosition >= 0);
/*Logger.print("onScroll");
        boolean enabled = false;

        if(view != null && view.getChildCount() > 0)
        {
            boolean firstItemVisible = view.getFirstVisiblePosition() == 0;

            boolean topOfFistItemVisible = view.getChildAt(0).getTop() == 0;

            enabled = firstItemVisible && topOfFistItemVisible;
        }

        homeActivity.setSwipeRefreshLayoutEnabled(enabled);*/
    }
}
