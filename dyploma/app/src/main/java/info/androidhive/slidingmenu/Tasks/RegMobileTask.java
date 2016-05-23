package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static info.androidhive.slidingmenu.Utils.FormatUtils.addQueryParameter;

public class RegMobileTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "RegMobileTask";

    private String loginUrl;
    private String userName;
    private String password;
    private String imei;
    public RegMobileTask(String loginUrl, String userName, String password, String imei) {
        this.loginUrl = loginUrl;
        this.userName = userName;
        this.password = password;
        this.imei = imei;
    }

    protected String doInBackground(String... urls) {
        try {
            Log.d(TAG, "Регистрируем мобильное устройство с imei = " + imei + " для пользователя: " + userName);
            String uri  = addQueryParameter(loginUrl, "logon_name", userName, true);
            uri = addQueryParameter(uri, "logon_pwd", password, false);
            uri = addQueryParameter(uri, "IMEI", imei, false);

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
            Log.e(TAG, "Ошибка при регистрации мобильного устройства: " + e.getLocalizedMessage());
            return null;
        }
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}