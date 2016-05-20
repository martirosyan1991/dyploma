package info.androidhive.slidingmenu.Utils;

import android.content.Context;

import com.dyploma.garik.dyploma.R;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import info.androidhive.slidingmenu.Tasks.LoadTask;

/**
 * Created by Harry on 20.05.2016.
 */
public class ServiceUtils {




    public  static Map<String, Integer> getQueueNumbers(Context context) {
        String number_query = null;
        try {
            number_query = new LoadTask().execute(context.getResources().getString(R.string.number_query)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return  FormatUtils.getQueueNumbers(number_query);
    }

    public  static int getQueueLoad(Context context) {
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
}
