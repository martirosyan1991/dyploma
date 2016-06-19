package com.pkmpei.mobile.Fragments;

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
import android.widget.Toast;

import com.dyploma.garik.dyploma.R;

import java.util.Map;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.Utils.FormatUtils;
import com.pkmpei.mobile.Utils.ServiceUtils;

public class QueueFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "QueueFragment";
    private LinearLayout queueLayout;
    private LinearLayout queueLoadLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public QueueFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);

        queueLayout = (LinearLayout) rootView.findViewById(R.id.queue_layout);
        queueLoadLayout = (LinearLayout) rootView.findViewById(R.id.queue_load_layout);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        refreshQueueStatus();

        return rootView;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshQueueStatus();
            }
        }, 1000);
    }

    /**
     * Обновление состояния очередей
     */
    private void refreshQueueStatus() {

        mSwipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "Обновление состояния очередей");
        // формируем список очередей по формату А - 10, Б - 15 и т.д.
        ListView queueList = (ListView) queueLayout.findViewById(R.id.queue_list);
        Map<String, Integer> queueNumbersMap = ServiceUtils.getQueueNumbers(getActivity(), new Callback<String>() {
            @Override
            public void call(String input) {
                getActivity().runOnUiThread(new Runnable() {
                                  public void run() {
                                      mSwipeRefreshLayout.setRefreshing(false);
                                  }
                              });
            }
        });
        int mapSize = queueNumbersMap.values().size();
        if (mapSize != 0 ) {
            Integer [] numbers = queueNumbersMap.values().toArray(new Integer[mapSize]);
            String [] queueLines = queueNumbersMap.keySet().toArray(new String[mapSize]);
            String [] queueTitles = new String[mapSize];
            for (int i = 0; i < mapSize; i++) {
                queueTitles[i] = queueLines[i] + " = " + numbers[i];
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, queueTitles);
            queueList.setAdapter(adapter);
        }

        mSwipeRefreshLayout.setRefreshing(true);
        int loadNumber = ServiceUtils.getQueueLoad(getActivity(), new Callback<String>() {
            @Override
            public void call(String input) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        LinearLayout queueLoadList = (LinearLayout) queueLoadLayout.findViewById(R.id.linear);
        queueLoadList.removeAllViews();
        if (loadNumber != -1) {
            // выводим загруженность очереди в формате человечков
            Log.d(TAG, "Выводим загруженность очереди в формате человечков, показатель равен " + loadNumber);
            for (int i = 0; i < loadNumber; i++) {
                ImageView imageView = new ImageView(new ContextThemeWrapper(getActivity(), R.style.AppTheme_OneColoredMan));
                int myColor;
                if (loadNumber <= 5) {
                    myColor = getResources().getColor(R.color.green_man_color);
                } else if (loadNumber > 5 && loadNumber < 8) {
                    myColor = getResources().getColor(R.color.yellow_man_color);
                } else {
                    myColor = getResources().getColor(R.color.red_man_color);
                }
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.green_man).mutate();
                drawable.setColorFilter(myColor, PorterDuff.Mode.SRC_ATOP);
                imageView.setImageDrawable(drawable);
                imageView.setImageDrawable(drawable);
                imageView.setLayoutParams(new DrawerLayout.LayoutParams(FormatUtils.convertDpToPixels(getActivity(), 25), FormatUtils.convertDpToPixels(getActivity(), 45)));
                queueLoadList.addView(imageView);
            }
        } else {
            Toast.makeText(getActivity(), "Ошибка при получении загружежнности очереди", Toast.LENGTH_SHORT).show();
            loadNumber = 0;
        }
        for (int i = loadNumber; i < 10; i++) {
            ImageView imageView = new ImageView(new ContextThemeWrapper(getActivity(), R.style.AppTheme_OneColoredMan));
            int myColor = getResources().getColor(R.color.light_grey_man);
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.green_man).mutate();
            drawable.setColorFilter(myColor, PorterDuff.Mode.SRC_ATOP);
            imageView.setImageDrawable(drawable);
            imageView.setImageDrawable(drawable);
            imageView.setLayoutParams(new DrawerLayout.LayoutParams(FormatUtils.convertDpToPixels(getActivity(), 25), FormatUtils.convertDpToPixels(getActivity(), 45)));
            queueLoadList.addView(imageView);
        }
    }
}