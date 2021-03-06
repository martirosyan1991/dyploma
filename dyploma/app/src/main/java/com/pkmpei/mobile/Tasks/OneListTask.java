package com.pkmpei.mobile.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.pkmpei.mobile.Callback;

public class OneListTask extends AsyncTask<String, Void, String> {

    private final Callback<String> callback;
    private static final String TAG = "OneListTask"
            ;

    public OneListTask(Callback<String> callback) {
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        try {
            Log.d(TAG, "Получения содержимого одного конкурсного списка.");
            URL url = new URL(urls[0]);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "windows-1251"));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            if (callback != null) {
                callback.call(response.toString());
            }
            return response.toString();
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