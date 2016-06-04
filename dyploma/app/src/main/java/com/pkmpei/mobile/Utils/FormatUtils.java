package com.pkmpei.mobile.Utils;

import android.content.Context;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pkmpei.mobile.News;

public class FormatUtils {

    private static final String TAG = "FormatUtils";

    public static int getLoadFactor(String load) {
        Log.d(TAG, "Обрабатываем загруженность очереди");
        Pattern p = Pattern.compile("STATUS: OK.*");
        Matcher matcher = p.matcher(load);
        if (matcher.matches()) {
            return Integer.parseInt(load.substring("STATUS: OK".length()));
        } else {
            Log.e(TAG, "Загруженность очереди не прошла проверку регулярным выражением: " + load);
            return -1;
        }
    }

    public static Map<String, Integer> getQueueNumbers(String input) {
        Log.d(TAG, "Получаем номера электронной очереди");
        Map<String, Integer> map = new TreeMap<>();
        Pattern p = Pattern.compile("STATUS: OK.*");
        Matcher matcher = p.matcher(input);
        if (matcher.matches()) {
            String result = input.substring("STATUS: OK".length());
            p = Pattern.compile("([А-Я]) - (\\d+)");
            String [] some = result.split(";");
            for (String s :some) {
                matcher = p.matcher(s);
                if (matcher.find()) {
                    map.put(matcher.group(1), Integer.parseInt(matcher.group(2)));
                }
            }
        }
        if (map.size() == 0) {
            Log.e(TAG, "Электронная очередь пустая, возможна ошибка в запросе");
        }
        return map;
    }

    public static List<News> getNews(String input) {
        Log.d(TAG, "Получаем список новостей");
        List<News> result = new LinkedList<>();
        String [] newsList = input.split("###");
        for (int i = 1; i < newsList.length; i++) {
            String news = newsList[i];
            String [] details = news.split("##");
            News n = new News();
            int j = 0;
            n.setId(Integer.parseInt(details[j++]));
            n.setDate(details[j++]);
            n.setTitle(details[j++]);
            n.setText(details[j++]);
            n.setExpandable("1".equals(details[j]));
            result.add(n);
        }
        return result;
    }

    public  static int convertDpToPixels(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static String addQueryParameter(String query, String parameterName, String parameterValue, boolean firstParameter) {
        return query + (firstParameter ? "?" : "&") +  parameterName + "=" + parameterValue;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() <= 0;
    }
}
