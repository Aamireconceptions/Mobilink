package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;

/**
 * @author Pehlaj Rai
 * @since 11/10/2014
 */

public abstract class BaseFragment extends Fragment implements OnClickListener {

    protected int layoutResId;

    protected Activity mActivity;

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(layoutResId, container, false);

        mActivity = getActivity();

        init(root);

        return root;
    }

    public abstract void init(View parent);

    @Override
    public void onClick(View v) {
    }

    public void hideKeyboard() {
        try {
            View view = mActivity.getCurrentFocus();
            if(view != null) {
                InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Window window = mActivity.getWindow();
            if(window != null) {
                window.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch(Throwable ignored) {}
    }
}
