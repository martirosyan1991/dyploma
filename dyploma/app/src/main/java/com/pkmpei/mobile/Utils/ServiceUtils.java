package com.pkmpei.mobile.Utils;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
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

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.News;
import com.pkmpei.mobile.PreStudent;
import com.pkmpei.mobile.Tasks.CheckAuthTask;
import com.pkmpei.mobile.Tasks.GetConcursTask;
import com.pkmpei.mobile.Tasks.GetGroupsTask;
import com.pkmpei.mobile.Tasks.GetNewsDetailsTask;
import com.pkmpei.mobile.Tasks.GetNewsTask;
import com.pkmpei.mobile.Tasks.LoadTask;
import com.pkmpei.mobile.Tasks.LogonTask;
import com.pkmpei.mobile.Tasks.LogoutTask;
import com.pkmpei.mobile.Tasks.OneListTask;
import com.pkmpei.mobile.UserPreferences;

/**
 * Created by Harry on 20.05.2016.
 */
public class ServiceUtils {


    private final static String TAG = "ServiceUtils";
    public static Map<String, Integer> getQueueNumbers(Context context, Callback<String> callback) {
        String number_query = null;
        try {
            number_query = new LoadTask(callback).execute(context.getResources().getString(R.string.number_query)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return  FormatUtils.getQueueNumbers(number_query);
    }

    public static int getQueueLoad(Context context, Callback<String> callback) {
        String load_query = null;
        try {
            load_query = new LoadTask(callback).execute(context.getResources().getString(R.string.load_query)).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при обработке запроса загруженности очереди", e);
        }
        return  FormatUtils.getLoadFactor(load_query);
    }

    public static Map<String, String> getLists(Context context, Callback<String> callback) {

        Log.d(TAG, "Получение всех конкурсных списков");
        Map<String, String> fullList = new HashMap<>();
        try {
            String baseUri = context.getResources().getString(R.string.list_base_uri);
            String load_query = new LoadTask(callback).execute(context.getResources().getString(R.string.lists_and_orders)).get();
            Document doc  = Jsoup.parse(load_query);
            Elements links = doc.select("a[href~=list[a-z]?[0-9]*[a-z].html]");


            for (int i = 0; i < links.size(); i++) {
                Document tempDoc = Jsoup.parse(new LoadTask(callback).execute(baseUri + links.get(i).attr("href")).get());
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

    public static List<News> getNews(Context context, Callback<String> callback) {

        Log.d(TAG, "Получение списка новостей");
        List<News> fullList = new LinkedList<>();
        try {
            String load_query = new GetNewsTask(callback).execute(context.getResources().getString(R.string.get_news)).get();
            fullList = FormatUtils.getNews(load_query);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении списка новостей: " + e.getLocalizedMessage());
        }
        return fullList;
    }

    public static News getNewsDetails(Context context, int newsId, Callback<String> callback) {
        Log.d(TAG, "Получение содержимого новости");
        try {
            String load_query = new GetNewsDetailsTask(newsId, callback)
                    .execute(context.getResources().getString(R.string.get_news_detail)).get();
            if (!Utils.isEmpty(load_query) && load_query.startsWith("1###")) {
                Log.d(TAG, "Содержимое новости получено");
                load_query = load_query.substring("1###".length());
                String [] newsContent = load_query.split("##");
                News news = new News();
                news.setId(Integer.parseInt(newsContent[0]));
                news.setDate(newsContent[1]);
                news.setTitle(newsContent[2]);
                news.setText(newsContent[3]);
                return news;
            }
            return null;
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении содержимого новости: " + e.getLocalizedMessage());
        }
        return null;
    }

    public static Map<String, String> getListsForCurrentUser(Context context, Callback<String> callback) {

        // TODO добавить обработку списка групп
        Log.d(TAG, "Получение конкурсных списков для текущего пользователя");
        Map<String, String> fullList = new HashMap<>();
        try {
            String load_query = new GetGroupsTask(callback).execute(context.getResources().getString(R.string.get_groups)).get();
            System.out.println("yyy loadQuery for groups = " + load_query);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении конкурсных списков: " + e.getLocalizedMessage());
        }
        return fullList;
    }

    public static String getConcursGroup(Context context, Callback<String> callback) {
        try {
            return new GetConcursTask(callback).execute(context.getResources().getString(R.string.get_concurs)).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении позиции абитуриента в конкурсной группе: " + e.getLocalizedMessage());
            return "";
        }
    }

    public static List<PreStudent> getOneList(String listUri, Callback<String> callback) {
        Log.d(TAG, "Получение содержимого списка: " + listUri);

        List<PreStudent> allPreStudents = new LinkedList<>();
        try {
            String load_query = new OneListTask(callback).execute(listUri).get();
            if (Utils.isEmpty(load_query)) {
                Log.e(TAG, "Ошибка при получении списка, результат пустоой");
                return allPreStudents;
            }
            Document doc  = Jsoup.parse(load_query);
            Elements links = doc.select("table[class=\"thin-grid competitive-group-table\"]");

            if (links.size() == 1) {
                Elements peopleList = links.get(0).child(0).children();
                for (int i = 2; i < peopleList.size(); i++) {
                    try {
                        Elements fields = peopleList.get(i).select("tr").select("td");
                        PreStudent preStudent = new PreStudent();
                        int ind = 0;
                        preStudent.setSum(Integer.parseInt(fields.get(ind++).text().replace("\u00A0","").trim()));
                        preStudent.setMath(Integer.parseInt(fields.get(ind++).text().replace("\u00A0","").trim()));
                        // Хак, есть таблицы, где не указаны баллы за физику
                        if (fields.size() == 10) {
                            preStudent.setPhysic(Integer.parseInt(fields.get(ind++).text().replace("\u00A0", "").trim()));
                        }
                        preStudent.setRussian(Integer.parseInt(fields.get(ind++).text().replace("\u00A0","").trim()));
                        preStudent.setId(fields.get(ind++).text().replace("\u00A0","").trim());
                        preStudent.setFio(fields.get(ind++).text());
                        preStudent.setBirthDate(fields.get(ind++).text());
                        preStudent.setCommon(fields.get(ind++).text());
                        preStudent.setDocumentStatus(fields.get(ind++).text());
                        preStudent.setComments(fields.get(ind).text());
                        allPreStudents.add(preStudent);
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка при парсинге данных абитуриента из списка: " + peopleList.get(i) +
                        "\nСообщение об ошибке: "+ e.getLocalizedMessage());
                    }
                }
            } else {
                Log.e(TAG, "Что-то не так со списком, количество подходящих таблиц : " + links.size());
            }
            if (allPreStudents.size() == 0) {
                Log.d(TAG, "Список студентов в конкурсной группе пустой");
            } else {
                Log.d(TAG, "Список студентов успешно загужен, количество элементов списка: " + allPreStudents.size());
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении содержимого списка студентов: " + e.getLocalizedMessage());
        }
        return allPreStudents;
    }


    public static void logon(String logon_query, String mobile_pwd, Callback<String> callback) {
        String FIOandBD = null;
        try {
            FIOandBD = new LogonTask(logon_query,
                    UserPreferences.getInstance().getImei(),mobile_pwd, callback).execute().get();

            if (!Utils.isEmpty(FIOandBD) && FIOandBD.startsWith("1")) {
                Log.d(TAG, "Авторизация пользователя прошла успешно, ФИО и даты рождения: " + FIOandBD);
                UserPreferences.getInstance().setFIO(FIOandBD.split(",")[1]);
                UserPreferences.getInstance().setBirthDate(FIOandBD.split(",")[2]);
            } else {
                Log.e(TAG, "Ошибка при авторизации пользоватлея: " + FIOandBD);
                UserPreferences.getInstance().setFIO("");
                UserPreferences.getInstance().setBirthDate("");
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при авторизации: " + FIOandBD);
        }
    }

    public static void checkAuth(String checkQuery, Callback<Boolean> callback) {
        String result = null;
        try {
            result = new CheckAuthTask(callback).execute(checkQuery).get();
            Log.d(TAG, "Запрос проверки авторизации прошел успешно: " + result);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при проверке авторизации: " + result + "{" + e.getLocalizedMessage() + "}");
        }
    }

    public static void logout(String checkQuery, Callback<String> callback) {
        String result = null;
        try {
            result = new LogoutTask(callback).execute(checkQuery).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при проверке авторизации: " + result + "{" + e.getLocalizedMessage() + "}");
        }
    }
}
