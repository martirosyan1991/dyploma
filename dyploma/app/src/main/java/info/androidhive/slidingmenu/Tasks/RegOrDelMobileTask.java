package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import info.androidhive.slidingmenu.Callback;
import info.androidhive.slidingmenu.Utils.FormatUtils;

import static info.androidhive.slidingmenu.Utils.FormatUtils.addQueryParameter;

public class RegOrDelMobileTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "RegOrDelMobileTask";
    private final Callback<String> callback;

    private String loginUrl;
    private String userName;
    private String password;
    private String imei;

    public RegOrDelMobileTask(String loginUrl, String userName, String password, String imei,
                              Callback<String> callback) {
        this.loginUrl = loginUrl;
        this.userName = userName;
        this.password = password;
        this.imei = imei;
        this.callback = callback;
    }

    protected String doInBackground(String... urls) {
        String result = null;
        try {
            Log.d(TAG, "Регистрируем/отвязываем мобильное устройство с imei = " + imei + " для пользователя: " + userName);
            String uri  = addQueryParameter(loginUrl, "logon_name", userName, true);
            uri = addQueryParameter(uri, "logon_pwd", password, false);
            uri = addQueryParameter(uri, "IMEI", imei, false);

            Connection.Response connection = Jsoup.connect(uri).execute();
            Document document = connection.parse();
            Log.d(TAG, "Запрос прошел успешно, результат: " + document.text());
            result = document.text();
            if (!FormatUtils.isEmpty(result) && result.startsWith("1") || callback != null) {
                callback.call(result);
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при регистрации/отвязке мобильного устройства: " + result +
                "{" + e.getLocalizedMessage() + "}");
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}