package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dyploma.garik.dyploma.R;
import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.PreStudent;
import com.pkmpei.mobile.Utils.ServiceUtils;

import java.util.List;

public class ConcursGroupFragment extends Fragment {

    private static final String TAG = "QueueFragment";

    public static final String GROUP_URI = "GROUP_URI";
    public ConcursGroupFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_concurs_group, container, false);

        Pair<GridLayout, List<PreStudent>> result = ServiceUtils.getOneList(getArguments().getString(GROUP_URI), null, getActivity());
        List<PreStudent> preStudents = result.second;
        GridLayout gridLayout = result.first;
        HorizontalScrollView concursGroupTable = (HorizontalScrollView) rootView.findViewById(R.id.concurs_group_table);



        if (preStudents.isEmpty()) {
            concursGroupTable.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Список поступающих пуст", Toast.LENGTH_SHORT).show();
            return rootView;
        }
        concursGroupTable.setVisibility(View.VISIBLE);
        concursGroupTable.addView(gridLayout);
        return rootView;
    }
}