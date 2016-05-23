package info.androidhive.slidingmenu.Utils;

import android.content.Context;
import android.util.Log;

import com.dyploma.garik.dyploma.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import info.androidhive.slidingmenu.PreStudent;
import info.androidhive.slidingmenu.Tasks.GetGroupsTask;
import info.androidhive.slidingmenu.Tasks.LoadTask;
import info.androidhive.slidingmenu.Tasks.LogonTask;
import info.androidhive.slidingmenu.Tasks.OneListTask;
import info.androidhive.slidingmenu.UserPreferences;

/**
 * Created by Harry on 20.05.2016.
 */
public class ServiceUtils {


    private final static String TAG = "ServiceUtils";
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

        Log.d(TAG, "Получение конкурсных списков для");
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
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении конкурсных списков: " + e.getLocalizedMessage());
        }
        return fullList;
    }

    public static Map<String, String> getListsForCurrentUser(Context context) {

        Log.d(TAG, "Получение конкурсных списков для текущего пользователя");
        Map<String, String> fullList = new HashMap<>();
        try {
            String load_query = new GetGroupsTask().execute(context.getResources().getString(R.string.get_groups)).get();
            System.out.println("yyy loadQuery for groups = " + load_query);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении конкурсных списков: " + e.getLocalizedMessage());
        }
        return fullList;
    }

    public static List<PreStudent> getOneList(String listUri) {
        Log.d(TAG, "Получение содержимого списка: " + listUri);

        List<PreStudent> allPreStudents = new LinkedList<>();
        try {
            String load_query = new OneListTask().execute(listUri).get();
            Document doc  = Jsoup.parse(load_query);
            Elements links = doc.select("table[class=\"thin-grid competitive-group-table\"]");

            if (links.size() == 1) {
                Elements peopleList = links.get(0).child(0).children();
                for (int i = 2; i < peopleList.size(); i++) {
                    Elements fields = peopleList.get(i).select("tr").select("td");
                    PreStudent preStudent = new PreStudent();
                    int ind = 0;
                    preStudent.setSum(Integer.parseInt(fields.get(ind++).text()));
                    preStudent.setMath(Integer.parseInt(fields.get(ind++).text()));
                    preStudent.setPhysic(Integer.parseInt(fields.get(ind++).text()));
                    preStudent.setRussian(Integer.parseInt(fields.get(ind++).text()));
                    preStudent.setId(Integer.parseInt(fields.get(ind++).text()));
                    preStudent.setFio(fields.get(ind++).text());
                    preStudent.setBirthDate(fields.get(ind++).text());
                    preStudent.setCommon(fields.get(ind++).text());
                    preStudent.setDocumentStatus(fields.get(ind++).text());
                    preStudent.setComments(fields.get(ind).text());
                    allPreStudents.add(preStudent);
                }
            }
            if (allPreStudents.size() == 0) {
                Log.d(TAG, "Список студентов в конкурсной группе пустой");
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении содержимого списка студентов: " + e.getLocalizedMessage());
        }
        return allPreStudents;
    }


    public static void logon(String logon_query, String mobile_pwd) {
        String FIOandBD = null;
        try {
            FIOandBD = new LogonTask(logon_query,
                    UserPreferences.getInstance().getImei(),mobile_pwd).execute().get();

            if (!FormatUtils.isEmpty(FIOandBD) && FIOandBD.startsWith("1")) {
                Log.d(TAG, "Авторизация пользователя прошла успешно, ФИО и даты рождения: " + FIOandBD);
                UserPreferences.getInstance().setFIO(FIOandBD.split(" ")[1]);
                UserPreferences.getInstance().setBirthDate(FIOandBD.split(" ")[2]);
            } else {
                throw  new ExecutionException(new Throwable());
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при авторизации: " + FIOandBD);
        }
    }
}
