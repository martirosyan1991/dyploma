package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyploma.garik.dyploma.R;

public class ConcursGroupFragment extends Fragment {

    private static final String TAG = "QueueFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public ConcursGroupFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_concurs_group, container, false);


        return rootView;
    }
}