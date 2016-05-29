package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class OneListTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "OneListTask"
            ;
    protected String doInBackground(String... urls) {
        try {
            Log.d(TAG, "Получения содержимого одного конкурсного списка.");
            Connection.Response connection = Jsoup.connect(urls[0]).execute();
            Document document = connection.parse();
            Log.d(TAG, "Запрос прошел успешно, результат: " + document.text());
            return document.text();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении конкурсного списка: " + e.getLocalizedMessage());
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}