package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import info.androidhive.slidingmenu.Utils.ServiceUtils;

public class AllListsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ListView groupList;
    SwipeRefreshLayout mSwipeRefreshLayout;
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
                List<PreStudent> allPreStudents = ServiceUtils.getOneList(((TextView) view).getText().toString());
            }
        });
        refreshData();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshGroupList);
        mSwipeRefreshLayout.setOnRefreshListener(this);



        return rootView;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    private void refreshData() {
        Map<String, String> listLinks = ServiceUtils.getLists(getActivity());
        int mapSize = listLinks.values().size();
        String [] numbers = listLinks.values().toArray(new String[mapSize]);
        String [] queueLines = listLinks.keySet().toArray(new String[mapSize]);
        String [] queueTitles = new String[mapSize];
        for (int i = 0; i < mapSize; i++) {
            queueTitles[i] = queueLines[i];
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_link, queueTitles);
        groupList.setAdapter(adapter);
        groupLinks.clear();
        groupLinks.putAll(listLinks);
    }
}
