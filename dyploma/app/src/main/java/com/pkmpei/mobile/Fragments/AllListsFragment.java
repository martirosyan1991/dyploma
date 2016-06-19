package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dyploma.garik.dyploma.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.ConcursGroup;
import com.pkmpei.mobile.Utils.ServiceUtils;

import org.jsoup.nodes.Element;

public class AllListsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ListView groupList;
    List<String> hrefs = new LinkedList<>();
    List<Pair<String, Map<String, List<Element>>>> listLinks;
    public AllListsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.all_lists, container, false);

        groupList = (ListView) rootView.findViewById(R.id.group_list);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                android.app.FragmentManager fragmentManager = getFragmentManager();
                Bundle arguments = new Bundle();
                arguments.putString(ConcursGroupFragment.GROUP_URI, "http://www.pkmpei.ru//inform/" + hrefs.get(position));
                Fragment fragment = new ConcursGroupFragment();
                fragment.setArguments(arguments);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack(null).commit();
                Toast.makeText(getActivity(), hrefs.get(position), Toast.LENGTH_SHORT).show();

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

                List<ConcursGroup> userGroups = ServiceUtils.getListsForCurrentUser(getActivity(), new Callback<String>() {
                    @Override
                    public void call(String input) {

                    }
                });

                List<String> linksFilter = new LinkedList<>();
                for (ConcursGroup group: userGroups) {
                    String link = "entrants_list";
                    if (group.isNeedDomitory()) {
                        link += "h";
                    }
                    link += group.getId() + ".html";
                    linksFilter.add(link);
                }

                List<Pair<String, Map<String, List<Element>>>> tempListLinks = ServiceUtils.getEntrantsLists(getActivity(), new Callback<String>() {
                    @Override
                    public void call(String input) {
                    }
                }, linksFilter);
                if (tempListLinks.size() == 0) {
                    return;
                }
                hrefs.clear();
                int hrefsCount = 0;
                listLinks = tempListLinks;
                groupList.setAdapter(null);
                List<String> queueTitles = new LinkedList<>();
                for (Pair<String, Map<String, List<Element>>> p: listLinks) {
                    Map<String, List<Element>> mapaaa = p.second;
                    for (String key: mapaaa.keySet()) {
                        List<Element> list = mapaaa.get(key);
                        for (Element e : list) {
                            queueTitles.add(p.first + "(" + key + ")" + e.text());
                            hrefs.add(hrefsCount++, e.attr("href"));
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_link, queueTitles);
                groupList.setAdapter(adapter);
            }
}
