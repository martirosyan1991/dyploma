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

import com.dyploma.garik.dyploma.R;
import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.PreStudent;
import com.pkmpei.mobile.Utils.ServiceUtils;

import java.util.List;

public class ConcursGroupFragment extends Fragment {

    private static final String TAG = "QueueFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public ConcursGroupFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_concurs_group, container, false);

        Pair<GridLayout, List<PreStudent>> result = ServiceUtils.getOneList("http://www.pkmpei.ru//inform/entrants_list6.html", null, getActivity());
        List<PreStudent> preStudents = result.second;
        GridLayout gridLayout = result.first;
        HorizontalScrollView concursGroupTable = (HorizontalScrollView) rootView.findViewById(R.id.concurs_group_table);



        if (preStudents.isEmpty()) {
            concursGroupTable.setVisibility(View.GONE);
            return rootView;
        }
        concursGroupTable.setVisibility(View.VISIBLE);
        concursGroupTable.addView(gridLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        /*for (PreStudent preStudent: preStudents) {
            TextView sumTextView = new TextView(getActivity(), null);
            TextView mathTextView = new TextView(getActivity(), null);
            TextView russianTextView = new TextView(getActivity(), null);
            TextView physicTextView = new TextView(getActivity(), null);
            TextView idTextView = new TextView(getActivity(), null);
            TextView fioTextView = new TextView(getActivity(), null);
            TextView birthDateTextView = new TextView(getActivity(), null);
            TextView commonTextView = new TextView(getActivity(), null);
            TextView docStateTextView = new TextView(getActivity(), null);
            TextView commentsTextView = new TextView(getActivity(), null);

            sumTextView.setLayoutParams(layoutParams);
            mathTextView.setLayoutParams(layoutParams);
            russianTextView.setLayoutParams(layoutParams);
            physicTextView.setLayoutParams(layoutParams);
            idTextView.setLayoutParams(layoutParams);
            fioTextView.setLayoutParams(layoutParams);
            birthDateTextView.setLayoutParams(layoutParams);
            commonTextView.setLayoutParams(layoutParams);
            docStateTextView.setLayoutParams(layoutParams);
            commentsTextView.setLayoutParams(layoutParams);

            sumTextView.setText(Integer.toString(preStudent.getSum()));
            mathTextView.setText(Integer.toString(preStudent.getMath()));
            russianTextView.setText(Integer.toString(preStudent.getRussian()));
            physicTextView.setText(Integer.toString(preStudent.getPhysic()));
            idTextView.setText(preStudent.getId());
            fioTextView.setText(preStudent.getFio());
            birthDateTextView.setText(preStudent.getBirthDate());
            commonTextView.setText(preStudent.getCommon());
            docStateTextView.setText(preStudent.getDocumentStatus());
            commentsTextView.setText(preStudent.getComments());

            concursGroupTable.addView(sumTextView);
            concursGroupTable.addView(mathTextView);
            concursGroupTable.addView(physicTextView);
            concursGroupTable.addView(russianTextView);
            concursGroupTable.addView(idTextView);
            concursGroupTable.addView(fioTextView);
            concursGroupTable.addView(birthDateTextView);
            concursGroupTable.addView(commonTextView);
            concursGroupTable.addView(docStateTextView);
            concursGroupTable.addView(commentsTextView);
        }*/
        return rootView;
    }
}