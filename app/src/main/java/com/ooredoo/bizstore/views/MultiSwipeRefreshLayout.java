/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ooredoo.bizstore.views;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

/**
 * Created by Babar on 06-Aug-15.
 */
/**
 * A descendant of {@link android.support.v4.widget.SwipeRefreshLayout} which supports multiple
 * child views triggering a refresh gesture. You set the views which can trigger the gesture via
 * {@link #setSwipeableChildrens(int...)}, providing it the child ids.
 */
public class MultiSwipeRefreshLayout extends SwipeRefreshLayout
{
    private View[] mSwipeableChildrens;

    public MultiSwipeRefreshLayout(Context context)
    {
        super(context);
    }

    public MultiSwipeRefreshLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Set the children which can trigger a refresh by swiping down when they are visible. These
     * views need to be a descendant of this view.
     */
    public void setSwipeableChildrens(final int... ids)
    {
        assert ids != null;

        // Iterate through the ids and find the Views
        mSwipeableChildrens = new View[ids.length];

        for(int i = 0; i < ids.length; i++)
        {
            mSwipeableChildrens[i] = findViewById(ids[i]);
        }
    }

    // BEGIN_INCLUDE(can_child_scroll_up)
    /**
     * This method controls when the swipe-to-refresh gesture is triggered. By returning false here
     * we are signifying that the view is in a state where a refresh gesture can start.
     *
     * <p>As {@link android.support.v4.widget.SwipeRefreshLayout} only supports one direct child by
     * default, we need to manually iterate through our swipeable children to see if any are in a
     * state to trigger the gesture. If so we return false to start the gesture.
     */
    @Override
    public boolean canChildScrollUp()
    {
        if(mSwipeableChildrens != null && mSwipeableChildrens.length > 0)
        {
            //Logger.print("AppBar top: "+ HomeActivity.appBarLayout.getTop());
            // Iterate through the scrollable children and check if any of them can not scroll up
            for(View view : mSwipeableChildrens)
            {
                AppBarLayout appBarLayout = HomeActivity.appBarLayout;

                if(appBarLayout != null)
                {
                    //Logger.print("AppBar: "+HomeActivity.appBarLayout);
                    if(view != null && view.isShown() && !canViewScrollUp(view)
                            && (appBarLayout != null && appBarLayout.getTop() == 0))
                    {
                        // If the view is shown, and can not scroll upwards, return false and start the
                        // gesture.
                        return false;
                    }
                }
                else
                {
                    if(view != null && view.isShown() && !canViewScrollUp(view))
                    {
                        // If the view is shown, and can not scroll upwards, return false and start the
                        // gesture.
                        return false;
                    }
                }

            }
        }

        return true;
    }
    // END_INCLUDE(can_child_scroll_up)

    // BEGIN_INCLUDE(can_view_scroll_up)
    /**
     * Utility method to check whether a {@link View} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
    private static boolean canViewScrollUp(View view)
    {
        if(Build.VERSION.SDK_INT >= 14)
        {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(view, -1);
        }
        else
        {
            if(view instanceof AbsListView)
            {
                // Pre-ICS we need to manually check the first visible item and the child view's top
                // value
                final AbsListView absListView = (AbsListView) view;

                return absListView.getChildCount() > 0 &&
                        (absListView.getFirstVisiblePosition() > 0
                          || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            }
            else
            {
                // For all other view types we just check the getScrollY() value
                return view.getScrollY() > 0;
            }
        }
    }


}
