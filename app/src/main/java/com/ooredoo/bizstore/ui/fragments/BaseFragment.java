package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    protected View view;

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(layoutResId, container, false);

        mActivity = getActivity();

        view = root;

        init(root);

        return root;
    }

    public abstract void init(View parent);

    @Override
    public void onClick(View v) {
    }

    public static void hideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            if(view != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Window window = activity.getWindow();
            if(window != null) {
                window.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch(Throwable ignored) {}
    }
}
