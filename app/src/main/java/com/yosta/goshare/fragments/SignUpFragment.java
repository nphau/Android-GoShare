package com.yosta.goshare.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yosta.goshare.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HenryPhuc on 3/6/2016.
 */
public class SignUpFragment extends Fragment {

    @Bind(R.id.txt_login)
    TextView txtLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        ButterKnife.bind(this, rootView);

        if (rootView.findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return rootView;
            }
        }

        OnCreateView();

        return rootView;
    }

    private void OnCreateView() {

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnLogIn();
            }
        });
    }

    private void OnLogIn() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(getActivity().getIntent().getExtras());
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .commit();
    }
}
