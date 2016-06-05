package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.UserPreferences;
import com.pkmpei.mobile.Utils.ServiceUtils;

public class SignedInFragment extends Fragment {

    private static final String TAG = "AuthorizationFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView fioTextView;
    private TextView birthDateTextView;

    public SignedInFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_signed_in, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshSignedInFragment);
        fioTextView = (TextView) rootView.findViewById(R.id.fioTextView);
        fioTextView.setText(UserPreferences.getInstance().getFIO());
        birthDateTextView = (TextView) rootView.findViewById(R.id.birthDateTextView);
        birthDateTextView.setText(UserPreferences.getInstance().getBirthDate());
        Button cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);
                ServiceUtils.logout(getResources().getString(R.string.logout),
                        new Callback<String>() {
                            @Override
                            public void call(String input) {
                                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove("PHPSESSID");
                                editor.commit();
                                swipeRefreshLayout.setRefreshing(false);
                                android.app.FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.frame_container, new AuthorizationFragment()).commit();
                            }
                        });
            }
        });

        return rootView;
    }
}
