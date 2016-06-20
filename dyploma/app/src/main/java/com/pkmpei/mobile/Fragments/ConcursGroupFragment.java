package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.dyploma.garik.dyploma.R;
import com.pkmpei.mobile.Utils.ServiceUtils;

public class ConcursGroupFragment extends Fragment {

    private static final String TAG = "QueueFragment";

    public static final String GROUP_URI = "GROUP_URI";
    public ConcursGroupFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_concurs_group, container, false);

        Pair<GridLayout, Integer> result = ServiceUtils.getOneList(getArguments().getString(GROUP_URI), null, getActivity());
        Integer preStudentsCount = result.second;
        GridLayout gridLayout = result.first;
        HorizontalScrollView concursGroupTable = (HorizontalScrollView) rootView.findViewById(R.id.concurs_group_table);



        if (preStudentsCount == 0) {
            concursGroupTable.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Список поступающих пуст", Toast.LENGTH_SHORT).show();
            return rootView;
        }
        concursGroupTable.setVisibility(View.VISIBLE);
        concursGroupTable.addView(gridLayout);
        return rootView;
    }
}