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

public class GetGroupsTask extends AsyncTask<String, Void, String> {

    public final Callback<String> callback;
    private static final String TAG = "GetGroupTask";

    public GetGroupsTask(Callback<String> callback) {
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        String result;
        try {
            Log.d(TAG, "Получение конкурсных групп для авторизованного пользователя");
            String sessionId = UserPreferences.getInstance().getPhpSessId();
            if (Utils.isEmpty(sessionId)) {
                Log.e(TAG, "Ошибка при получении списка конкурсных групп, пользователь не авторизован");
                return "";
            }
            Connection.Response connection = Jsoup.connect(urls[0]).cookie("PHPSESSID", sessionId).execute();
            Document document = connection.parse();
            result = document.text();
            Log.d(TAG, "Запрос прошел успешно, результат: " + result);
            if (callback != null) {
                callback.call(result);
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении списка конкурсных групп: " + e.getLocalizedMessage());
            return "";
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}