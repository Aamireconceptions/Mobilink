package com.ooredoo.bizstore.interfaces;

/**
 * Created by Babar on 28-Jul-15.
 */
public interface OnDealsTaskFinishedListener
{
    void onRefreshCompleted();

    void onHaveDeals();

    void onNoDeals(int stringResId);
}
