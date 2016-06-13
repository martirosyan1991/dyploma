package com.pkmpei.mobile.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.UserPreferences;
import com.pkmpei.mobile.Utils.FormatUtils;
import com.pkmpei.mobile.Utils.Utils;

public class CheckAuthTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "LogonTask";

    Callback<Boolean> callback;

    public CheckAuthTask(Callback<Boolean> callback) {
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        boolean result;
        try {
            String sessionId = UserPreferences.getInstance().getPhpSessId();
            Log.d(TAG, "Начало выполнения запроса checkAuth с phpsessid = " + sessionId);
            if (Utils.isEmpty(sessionId)) {
                Log.e(TAG, "Ошибка при проверке прав пользователя, пользователь не авторизован");
                return "";
            }
            Connection.Response connection = Jsoup.connect(urls[0]).cookie("PHPSESSID", sessionId).execute();
            Document document = connection.parse();
            result = document.text().indexOf("[bdt] =>") - document.text().indexOf("[fio] =>") > 1;
            if (callback != null) {
                callback.call(result);
            }
            Log.d(TAG, "Запрос прошел успешно, результат: " + document.text());
            return document.text();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка выполнения запроса: " + e.getLocalizedMessage());
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}