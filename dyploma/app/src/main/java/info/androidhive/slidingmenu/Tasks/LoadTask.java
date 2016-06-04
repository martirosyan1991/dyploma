package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.androidhive.slidingmenu.Callback;

public class LoadTask extends AsyncTask<String, Void, String> {

    private final Callback<String> callback;
    private static final String TAG = "LoadTask";

    public LoadTask(Callback<String> callback) {
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        String result;
        try {
            Log.d(TAG, "Начало выполнения запроса: " + urls[0]);
            Connection.Response connection = Jsoup.connect(urls[0]).execute();
            Document document = connection.parse();
            result = document.text();
            Log.d(TAG, "Запрос прошел успешно, результат: " + result);
            if (callback != null) {
                callback.call(result);
            }
            return result;
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