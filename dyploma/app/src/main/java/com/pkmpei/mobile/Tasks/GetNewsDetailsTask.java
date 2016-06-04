package com.pkmpei.mobile.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.UserPreferences;
import com.pkmpei.mobile.Utils.FormatUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static com.pkmpei.mobile.Utils.FormatUtils.addQueryParameter;

public class GetNewsDetailsTask extends AsyncTask<String, Void, String> {

    private final Callback<String> callback;
    private static final String TAG = "GetNewsDetailsTask";

    private int newsId;

    public GetNewsDetailsTask(int newsId, Callback<String> callback) {
        this.newsId = newsId;
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        String result;
        try {
            Log.d(TAG, "Получение содержимого новости");
            String uri  = addQueryParameter(urls[0], "id", Integer.toString(newsId), true);
            String sessionId = UserPreferences.getInstance().getPhpSessId();
            Connection.Response connection;
            if (FormatUtils.isEmpty(sessionId)) {
                Log.d(TAG, "Пользователь не авторизован, ид сессии не передается");
                connection = Jsoup.connect(uri).execute();
            } else {
                connection = Jsoup.connect(uri).cookie("PHPSESSID", sessionId).execute();
            }

            Document document = connection.parse();
            result = document.toString();
            Log.d(TAG, "Запрос прошел успешно, результат: " + result);
            if (callback != null && result.startsWith("1")) {
                callback.call(result);
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении списка новостей: " + e.getLocalizedMessage());
            return "";
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}