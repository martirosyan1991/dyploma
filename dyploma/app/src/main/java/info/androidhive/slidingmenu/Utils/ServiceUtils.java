package info.androidhive.slidingmenu.Utils;

import android.content.Context;

import com.dyploma.garik.dyploma.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import info.androidhive.slidingmenu.Tasks.LoadTask;

/**
 * Created by Harry on 20.05.2016.
 */
public class ServiceUtils {

    public static Map<String, Integer> getQueueNumbers(Context context) {
        String number_query = null;
        try {
            number_query = new LoadTask().execute(context.getResources().getString(R.string.number_query)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return  FormatUtils.getQueueNumbers(number_query);
    }

    public static int getQueueLoad(Context context) {
        String load_query = null;
        try {
            load_query = new LoadTask().execute(context.getResources().getString(R.string.load_query)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return  FormatUtils.getLoadFactor(load_query);
    }

    public static Map<String, String> getLists(Context context) {

        Map<String, String> fullList = new HashMap<>();
        try {
            String baseUri = context.getResources().getString(R.string.list_base_uri);
            String load_query = new LoadTask().execute(context.getResources().getString(R.string.lists_and_orders)).get();
            Document doc  = Jsoup.parse(load_query);
            Elements links = doc.select("a[href~=list[a-z]?[0-9]*[a-z].html]");


            for (int i = 0; i < links.size(); i++) {
                Document tempDoc = Jsoup.parse(new LoadTask().execute(baseUri + links.get(i).attr("href")).get());
                Elements internalLinks = tempDoc.select("a[href~=list[a-z]?[0-9]*[a-z].html]");
                for (int j = 0; j < internalLinks.size(); j++) {
                    fullList.put(baseUri + internalLinks.get(j).attr("href"), internalLinks.get(j).text());
                }
            }

            System.out.println("yyy links = " + links);
            System.out.println("yyy doc = " + doc);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return fullList;
    }
}
