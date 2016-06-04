package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.androidhive.slidingmenu.Callback;
import info.androidhive.slidingmenu.UserPreferences;
import info.androidhive.slidingmenu.Utils.FormatUtils;

public class GetNewsTask extends AsyncTask<String, Void, String> {

    private final Callback<String> callback;
    private static final String TAG = "GetNewsTask";

    public GetNewsTask(Callback<String> callback) {
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        String result;
        try {
            Log.d(TAG, "Получение списка новостей");
            String sessionId = UserPreferences.getInstance().getPhpSessId();
            Connection.Response connection;
            if (FormatUtils.isEmpty(sessionId)) {
                Log.d(TAG, "Пользователь не авторизован, ид сессии не передается");
                connection = Jsoup.connect(urls[0]).execute();
            } else {
                connection = Jsoup.connect(urls[0]).cookie("PHPSESSID", sessionId).execute();
            }

            Document document = connection.parse();
            result = document.text();
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