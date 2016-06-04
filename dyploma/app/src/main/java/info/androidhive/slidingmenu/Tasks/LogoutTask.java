package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.androidhive.slidingmenu.Callback;
import info.androidhive.slidingmenu.UserPreferences;
import info.androidhive.slidingmenu.Utils.FormatUtils;

public class LogoutTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "LogoutTask";

    Callback<String> callback;

    public LogoutTask(Callback<String> callback) {
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        try {
            String sessionId = UserPreferences.getInstance().getPhpSessId();
            Log.d(TAG, "Начало выполнения запроса logout с phpsessid = " + sessionId);
            if (FormatUtils.isEmpty(sessionId)) {
                Log.e(TAG, "Ошибка при проверке прав пользователя, пользователь не авторизован");
                return "";
            }
            Connection.Response connection = Jsoup.connect(urls[0]).cookie("PHPSESSID", sessionId).execute();
            Document document = connection.parse();
            if (callback != null) {
                callback.call(document.text());
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