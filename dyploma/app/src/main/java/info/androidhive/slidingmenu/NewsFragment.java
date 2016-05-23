package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dyploma.garik.dyploma.R;

import java.util.List;
import java.util.Map;

import info.androidhive.slidingmenu.Utils.FormatUtils;
import info.androidhive.slidingmenu.Utils.ServiceUtils;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "QueueFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public NewsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);


        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_news);
        mSwipeRefreshLayout.setOnRefreshListener(this);
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
        }, 3000);
    }

    /**
     * Обновление состояния очередей
     */
    private void refreshNewsList() {

        Log.d(TAG, "Обновление списка новостей");
        ListView queueList = (ListView) mSwipeRefreshLayout.findViewById(R.id.news_list);
        List<News> newsList = ServiceUtils.getNews(getActivity());
        int listSize = newsList.size();
        String [] queueTitles = new String[listSize];
        for (int i = 0; i < listSize; i++) {
            queueTitles[i] = newsList.get(i).getTitle();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, queueTitles);
        queueList.setAdapter(adapter);
    }
}