package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dyploma.garik.dyploma.R;

import java.util.LinkedList;
import java.util.List;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.MainActivity;
import com.pkmpei.mobile.News;
import com.pkmpei.mobile.Utils.ServiceUtils;
import com.pkmpei.mobile.adapter.NewsTitleAdapter;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "QueueFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView newsListView;

    private List<Integer> newsIds = new LinkedList<>();
    public NewsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);


        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_news);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        newsListView = (ListView) rootView.findViewById(R.id.news_list);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                Bundle arguments = new Bundle();
                arguments.putInt(NewsDetailsFragment.NEWS_ID_KEY, newsIds.get(position));
                Fragment fragment = new NewsDetailsFragment();
                fragment.setArguments(arguments);
                ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack(null).commit();
            }
        });
        refreshNewsList();
        return rootView;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshNewsList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    /**
     * Обновление состояния очередей
     */
    private void refreshNewsList() {

        Log.d(TAG, "Обновление списка новостей");
        List<News> newsList = ServiceUtils.getNews(getActivity(), new Callback<String>() {
            @Override
            public void call(String input) {

            }
        });
        int listSize = newsList.size();
        if (listSize == 0) {
            Toast.makeText(getActivity(), "Список новостей пуст", Toast.LENGTH_SHORT).show();
            return;
        }
        String [] queueTitles = new String[listSize];
        newsIds.clear();
        for (int i = 0; i < listSize; i++) {
            queueTitles[i] = newsList.get(i).getTitle();
            newsIds.add(i, newsList.get(i).getId());
        }
        NewsTitleAdapter adapter = new NewsTitleAdapter(getActivity(), newsList);
        newsListView.setAdapter(adapter);
    }
}