package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import info.androidhive.slidingmenu.UserPreferences;
import info.androidhive.slidingmenu.Utils.FormatUtils;

import static info.androidhive.slidingmenu.Utils.FormatUtils.addQueryParameter;

public class GetConcursTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "GetConcursTask";

    protected String doInBackground(String... urls) {
        try {
            Log.d(TAG, "Получение позиции абитуриента в конкурсных группах");
            String sessionId = UserPreferences.getInstance().getPhpSessId();
            if (FormatUtils.isEmpty(sessionId)) {
                Log.e(TAG, "Ошибка при получении позиции абитуриента в конкурсных группах, пользователь не авторизован");
                return "";
            }
            String uri  = addQueryParameter(urls[0], "PHPSESSID", sessionId, true);
            URL url = new URL(uri);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } catch (Exception e) {
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}