package info.androidhive.slidingmenu.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Harry on 14.04.2016.
 */
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
        Map<String, Integer> map = new HashMap<>();
        Pattern p = Pattern.compile("STATUS: OK.*");
        Matcher matcher = p.matcher(input);
        if (matcher.matches()) {
            String result = input.substring("STATUS: OK".length());
            p = Pattern.compile("([А-Я]) - (\\d+)");
            String [] some = result.split(";");
            for (String s :some) {
                matcher = p.matcher(s);
                if (matcher.find()) {
                    System.out.println("yyy group = " + matcher.group());

                }
            }
        }
        return map;
    }
}
