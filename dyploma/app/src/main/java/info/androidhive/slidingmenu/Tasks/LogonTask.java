package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.androidhive.slidingmenu.Callback;
import info.androidhive.slidingmenu.UserPreferences;
import info.androidhive.slidingmenu.Utils.FormatUtils;

import static info.androidhive.slidingmenu.Utils.FormatUtils.addQueryParameter;

public class LogonTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "LogonTask";
    private final Callback<String> callback;
    private String logonUrl;
    private String mobilePassword;
    private String imei;
    public LogonTask(String logonUrl, String imei, String mobilePassword, Callback<String> callback) {
        this.logonUrl = logonUrl;
        this.imei = imei;
        this.mobilePassword = mobilePassword;
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        try {
            Log.d(TAG, "Начало выполнения запроса logon с imei = " + imei);
            String uri  = addQueryParameter(logonUrl, "IMEI", imei, true);
            uri = addQueryParameter(uri, "mobile_pwd", mobilePassword, false);

            Connection.Response connection = Jsoup.connect(uri).execute();
            Document document = connection.parse();

            if (document.text().startsWith("1")) {
                Log.d(TAG, "Авторизация мобильного устройства прошла успешно");
                String sessionId = connection.cookie("PHPSESSID");
                if (!FormatUtils.isEmpty(sessionId)) {
                    Log.d(TAG, "Id сессии получен");
                    UserPreferences.getInstance().setPhpSessId(connection.cookie("PHPSESSID"));
                } else {
                    Log.d(TAG, "Id сессии не был получен");
                }
                if (callback != null) {
                    callback.call(document.text());
                }
            }
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