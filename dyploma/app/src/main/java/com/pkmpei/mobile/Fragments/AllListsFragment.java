package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dyploma.garik.dyploma.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.ConcursGroup;
import com.pkmpei.mobile.UserPreferences;
import com.pkmpei.mobile.Utils.ServiceUtils;
import com.pkmpei.mobile.Utils.Utils;
import com.pkmpei.mobile.adapter.GroupTitleAdapter;

import org.jsoup.nodes.Element;

public class AllListsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ListView groupList;
    List<String> hrefs = new LinkedList<>();
    List<Pair<String, Map<String, List<Element>>>> listLinks;

    public AllListsFragment() {
    }

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

            }
        });

        ((TextView) rootView.findViewById(R.id.all_lists_link)).setMovementMethod((LinkMovementMethod.getInstance()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
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
        if (Utils.isEmpty(UserPreferences.getInstance().getFIO())) {
            Toast.makeText(getActivity(), "Пользовател не авторизован, список групп не может быть получен", Toast.LENGTH_LONG).show();
            return;
        }
        List<ConcursGroup> userGroups = ServiceUtils.getListsForCurrentUser(getActivity(), new Callback<String>() {
            @Override
            public void call(String input) {

            }
        });

        List<String> linksFilter = new LinkedList<>();
        for (ConcursGroup group : userGroups) {
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
        }, null);
        if (tempListLinks.size() == 0) {
            Toast.makeText(getActivity(), "Список групп абитуриента - пуст", Toast.LENGTH_SHORT).show();
            return;
        }
        hrefs.clear();
        int hrefsCount = 0;
        listLinks = tempListLinks;
        groupList.setAdapter(null);
        List<String> queueTitles = new LinkedList<>();
        for (Pair<String, Map<String, List<Element>>> p : listLinks) {
            Map<String, List<Element>> mapaaa = p.second;
            for (String key : mapaaa.keySet()) {
                List<Element> list = mapaaa.get(key);
                for (Element e : list) {
                    queueTitles.add(key + "(" + e.text().trim() + ")");
                    hrefs.add(hrefsCount++, e.attr("href"));
                }
            }
        }
        GroupTitleAdapter adapter = new GroupTitleAdapter(getActivity(), queueTitles);
        groupList.setAdapter(adapter);
    }
}
