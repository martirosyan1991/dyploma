package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.PreStudent;
import com.pkmpei.mobile.Utils.ServiceUtils;

public class AllListsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ListView groupList;
    Map<String, String> groupLinks = new TreeMap<>();
    public AllListsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.all_lists, container, false);

        groupList = (ListView) rootView.findViewById(R.id.group_list);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                android.util.Pair<GridLayout, List<PreStudent>> result = ServiceUtils.getOneList(((TextView) view).getText().toString(),
                        new Callback<String>() {
                            @Override
                            public void call(String input) {

                            }
                        }, getActivity());
                List<PreStudent> allPreStudents = result.second;
                GridLayout titleGridLayout = result.first;

                titleGridLayout.setVisibility(View.GONE);

            }
        });
        refreshData();

        return rootView;
    }

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                    }
                }, 1000);
            }

            private void refreshData() {
                Map<String, String> listLinks = ServiceUtils.getListsForCurrentUser(getActivity(), new Callback<String>() {
                    @Override
                    public void call(String input) {
                    }
                });
                int mapSize = listLinks.values().size();
                String[] queueLines = listLinks.keySet().toArray(new String[mapSize]);
                String[] queueTitles = new String[mapSize];
                for (int i = 0; i < mapSize; i++) {
                    queueTitles[i] = queueLines[i];
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_link, queueTitles);
                groupList.setAdapter(adapter);
                groupLinks.clear();
                groupLinks.putAll(listLinks);

                ServiceUtils.getConcursGroup(getActivity(), new Callback<String>() {
                    @Override
                    public void call(String input) {

                    }
                });

            }
}
