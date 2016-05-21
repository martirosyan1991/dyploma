package info.androidhive.slidingmenu.Tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static info.androidhive.slidingmenu.Utils.FormatUtils.addQueryParameter;

public class LogonTask extends AsyncTask<String, Void, String> {

    private String logonUrl;
    private String mobilePassword;
    private String imei;
    public LogonTask(String logonUrl, String imei, String mobilePassword) {
        this.logonUrl = logonUrl;
        this.imei = imei;
        this.mobilePassword = mobilePassword;
    }

    protected String doInBackground(String... urls) {
        try {
            String uri  = addQueryParameter(logonUrl, "IMEI", imei, true);
            uri = addQueryParameter(uri, "mobile_pwd", mobilePassword, false);

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
            System.out.println("yyy result = " + response);
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