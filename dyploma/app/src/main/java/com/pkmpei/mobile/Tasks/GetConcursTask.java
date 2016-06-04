package com.pkmpei.mobile.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.UserPreferences;
import com.pkmpei.mobile.Utils.FormatUtils;

public class GetConcursTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetConcursTask";

    private final Callback<String> callback;

    public GetConcursTask(Callback<String> callback) {
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        String result;
        try {
            Log.d(TAG, "Получение позиции абитуриента в конкурсных группах");
            String sessionId = UserPreferences.getInstance().getPhpSessId();
            if (FormatUtils.isEmpty(sessionId)) {
                Log.e(TAG, "Ошибка при получении позиции абитуриента в конкурсных группах, пользователь не авторизован");
                return "";
            }
            Connection.Response connection = Jsoup.connect(urls[0]).cookie("PHPSESSID", sessionId).execute();
            Document document = connection.parse();
            result = document.text();
            Log.d(TAG, "Запрос прошел успешно, результат: " + result);
            if (callback != null && result.startsWith("1")) {
                callback.call(result);
            }
            return document.text();
        } catch (Exception e) {
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}