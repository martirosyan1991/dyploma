package com.pkmpei.mobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.News;
import com.pkmpei.mobile.Utils.ServiceUtils;

public class NewsDetailsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "QueueFragment";

    public static final String NEWS_ID_KEY = "NEWS_ID_KEY";
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView newsText;
    private TextView newsTitle;
    private TextView newsDate;
    private int newsId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_details, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_news_details);
        swipeRefreshLayout.setOnRefreshListener(this);
        newsText = (TextView) rootView.findViewById(R.id.news_details_text_view);
        newsDate = (TextView) rootView.findViewById(R.id.news_date);
        newsTitle = (TextView) rootView.findViewById(R.id.news_title);
        newsId = getArguments().getInt(NEWS_ID_KEY);

        refreshOneNews();
        return rootView;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshOneNews();
            }
        }, 1000);
    }

    /**
     * Обновление ссодержание новости
     */
    private void refreshOneNews() {
        Log.d(TAG, "Обновление содержимого новости №" + newsId);
        swipeRefreshLayout.setRefreshing(true);
        News news = ServiceUtils.getNewsDetails(getActivity(), newsId, new Callback<String>() {
            @Override
            public void call(String input) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (news != null) {
            newsTitle.setText(news.getTitle());
            newsDate.setText(news.getDate());
            newsText.setText(Html.fromHtml(news.getText()));
        } else {
            newsTitle.setText("");
            newsDate.setText("");
            newsText.setText("Ошибка при получении содержимого новости");
        }
    }
}