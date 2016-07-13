package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.ui.activities.SignUpActivity;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.FragmentUtils;

import java.lang.reflect.Type;

import static com.ooredoo.bizstore.utils.FragmentUtils.addFragmentWithBackStack;
import static com.ooredoo.bizstore.utils.FragmentUtils.replaceFragmentWithBackStack;

/**
 * Created by Babar on 31-May-16.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       MainActivity mainActivity = (MainActivity) getActivity();
       mainActivity.toolbar.setVisibility(View.GONE);

        Button btSignIn = (Button) view.findViewById(R.id.signin);
        btSignIn.setOnClickListener(this);


        Button btSignUp = (Button) view.findViewById(R.id.signup);
        btSignUp.setOnClickListener(this);

        if(BizStore.getLanguage().equals("en"))
        {
         btSignIn.setSelected(true);
        }
        else
        {
            btSignUp.setSelected(true);
        }

        TextView tvLabel1 = (TextView) view.findViewById(R.id.label_1);
        TextView tvDays = (TextView) view.findViewById(R.id.days);
        TextView tvFree = (TextView) view.findViewById(R.id.free);
        TextView tvOnOoredoo = (TextView) view.findViewById(R.id.on_ooredoo);
        TextView tvFreeTrial = (TextView) view.findViewById(R.id.free_trail);
        tvFreeTrial.setMovementMethod(LinkMovementMethod.getInstance());

        FontUtils.setFontWithStyle(getActivity(), tvLabel1, Typeface.BOLD);
        FontUtils.setFontWithStyle(getActivity(), tvDays, Typeface.BOLD);
        FontUtils.setFontWithStyle(getActivity(), tvFree, Typeface.BOLD);
        FontUtils.setFontWithStyle(getActivity(), tvOnOoredoo, Typeface.BOLD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signin:

                SignUpFragment signUpFragment = new SignUpFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("is_signin", true);
                signUpFragment.setArguments(bundle1);
               replaceFragmentWithBackStack((AppCompatActivity) getActivity(), R.id.fragment_container,
                       signUpFragment, "signup_fragment");

                break;
            case R.id.signup:

                FragmentUtils.replaceFragmentWithBackStack((AppCompatActivity) getActivity(),
                        R.id.fragment_container,
                        new SubscriptionPlansFragment(),
                        "subscription_fragment");

                break;
        }
    }

}