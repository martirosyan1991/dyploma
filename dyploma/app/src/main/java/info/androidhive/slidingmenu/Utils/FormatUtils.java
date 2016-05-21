package info.androidhive.slidingmenu.Utils;

import android.content.Context;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

    public static int getLoadFactor(String load) {
        Pattern p = Pattern.compile("STATUS: OK.*");
        Matcher matcher = p.matcher(load);
        if (matcher.matches()) {
            System.out.println("yyy loadSubstring = " + load.substring("STATUS: OK".length()));
            return Integer.parseInt(load.substring("STATUS: OK".length()));
        } else {
            return -1;
        }
    }

    public static Map<String, Integer> getQueueNumbers(String input) {
        Map<String, Integer> map = new TreeMap<>();
        Pattern p = Pattern.compile("STATUS: OK.*");
        Matcher matcher = p.matcher(input);
        if (matcher.matches()) {
            String result = input.substring("STATUS: OK".length());
            p = Pattern.compile("([А-Я]) - (\\d+)");
            String [] some = result.split(";");
            for (String s :some) {
                matcher = p.matcher(s);
                if (matcher.find()) {
                    map.put(matcher.group(1), Integer.parseInt(matcher.group(2)));
                }
            }
        }
        return map;
    }

    public  static int convertDpToPixels(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static String addQueryParameter(String query, String parameterName, String parameterValue, boolean firstParameter) {
        return query + (firstParameter ? "?" : "&") +  parameterName + "=" + parameterValue;
    }
}
