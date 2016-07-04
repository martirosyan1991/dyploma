package com.pkmpei.mobile.Utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.pkmpei.mobile.Callback;
import com.pkmpei.mobile.ConcursGroup;
import com.pkmpei.mobile.News;
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
            number_query = new LoadTask(callback, 1).execute(context.getResources().getString(R.string.number_query)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return  FormatUtils.getQueueNumbers(number_query);
    }

    public static int getQueueLoad(Context context, Callback<String> callback) {
        String load_query = null;
        try {
            load_query = new LoadTask(callback, 1).execute(context.getResources().getString(R.string.load_query)).get();
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
            String load_query = new LoadTask(callback, 0).execute(context.getResources().getString(R.string.lists_and_orders)).get();
            Document doc  = Jsoup.parse(load_query);
            Elements links = doc.select("a[href~=list[a-z]?[0-9]*[a-z].html]");


            for (int i = 0; i < links.size(); i++) {
                Document tempDoc = Jsoup.parse(new LoadTask(callback, 0).execute(baseUri + links.get(i).attr("href")).get());
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

    public static List<Pair<String,Map<String, List<Element>>>> getEntrantsLists(Context context, Callback<String> callback, List<String> filter) {

        Log.d(TAG, "Получение списков, подавших документы");
        List<Pair<String, Map<String, List<Element>>>> fullList = new LinkedList<>();
        try {
            String load_query = new LoadTask(callback, 0).execute(context.getResources().getString(R.string.entrants_lists)).get();
            if (Utils.isEmpty(load_query)) {
                return fullList;
            }
            Document doc  = Jsoup.parse(load_query);

            Elements allElements = doc.getAllElements();
            Elements tableTitles = doc.select("[class=title2]");
            for (int i = 0; i < tableTitles.size(); i++) {
                int indexOfTitle = allElements.indexOf(tableTitles.get(i));
                if (indexOfTitle == -1) {
                    continue;
                }
                int j = 1;
                while (j < 5) {
                    Element currentTable = allElements.get(indexOfTitle + (j++));
                    if ("table".equalsIgnoreCase(currentTable.tag().toString())) {
                        Map<String, List<Element>> tableMap = new HashMap<>();
                        Elements links = currentTable.getAllElements().select("a[href~=entrants_list[a-z]?[0-9]*[a-z]?.html]");
                        for (int k = 0; k < links.size(); k++) {
                            Element currentList = links.get(k);
                            String link = currentList.attr("href");
                            if (filter != null && !filter.contains(link)) {
                                continue;
                            }
                            String groupTitle = currentList.parent().parent().getAllElements().get(1).text();
                            if (tableMap.get(groupTitle) != null) {
                                List<Element> groupLinks = new LinkedList<>(tableMap.get(groupTitle));
                                groupLinks.add(currentList);
                                tableMap.remove(groupTitle);
                                tableMap.put(groupTitle, groupLinks);
                            } else {
                                tableMap.put(groupTitle,
                                        Collections.singletonList(links.get(k)));
                            }
                        }
                        fullList.add(new Pair<>(tableTitles.get(i).text(), tableMap));
                        j = 6;
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении списков, подавших документы", e);
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
                if (newsContent.length > 3) {
                    news.setText(newsContent[3]);
                } else {
                    news.setText("");
                }
                return news;
            }
            return null;
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении содержимого новости: " + e.getLocalizedMessage());
        }
        return null;
    }

    public static List<ConcursGroup> getListsForCurrentUser(Context context, Callback<String> callback) {

        // TODO добавить обработку списка групп
        Log.d(TAG, "Получение конкурсных списков для текущего пользователя");
        List<ConcursGroup> fullList = new LinkedList<>();
        try {
            String load_query = new GetGroupsTask(callback).execute(context.getResources().getString(R.string.get_groups)).get();
            if (Utils.isEmpty(load_query) || !load_query.startsWith("1")) {
                return fullList;
            }
            String[] groups = load_query.split("###");
            for (int i = 1; i < groups.length; i++) {
                String[] grouProperties = groups[i].split(";");
                if (grouProperties.length != 3) {
                    continue;
                }
                ConcursGroup group = new ConcursGroup();
                group.setId(Integer.parseInt(grouProperties[0]));
                group.setName(grouProperties[1]);
                group.setNeedDomitory("1".equals(grouProperties[2]));
                fullList.add(group);
            }
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

    public static Pair<TableLayout, Integer> getOneList(String listUri, Callback<String> callback, Context context) {
        Log.d(TAG, "Получение содержимого списка: " + listUri);

        Integer preStudentsCount = 0;
        TableLayout tableLayout = (TableLayout) LayoutInflater.from(context).inflate(R.layout.concurs_group_table_layout, null);
        Pair<TableLayout, Integer> result = new Pair<>(tableLayout, preStudentsCount);
        try {
            String load_query = new OneListTask(callback).execute(listUri).get();
            if (Utils.isEmpty(load_query)) {
                Log.e(TAG, "Ошибка при получении списка, результат пустоой");
                return result;
            }
            Document doc  = Jsoup.parse(load_query);
            Elements links = doc.select("table[class=\"thin-grid competitive-group-table\"]");

            if (links.size() == 1) {
                List<String> columnTitles = new LinkedList<>();
                Elements peopleList = links.get(0).child(0).children();

                preStudentsCount = processFirstTitleRow(context, tableLayout, columnTitles, peopleList);

                /**/
            } else {
                Log.e(TAG, "Что-то не так со списком, количество подходящих таблиц : " + links.size());
            }
            if (preStudentsCount == 0) {
                Log.d(TAG, "Список студентов в конкурсной группе пустой");
            } else {
                result = new Pair<>(tableLayout, preStudentsCount);
                Log.d(TAG, "Список студентов успешно загужен, количество элементов списка: " + preStudentsCount);
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при получении содержимого списка студентов: " + e.getLocalizedMessage());
        }
        return result;
    }

    private static int processFirstTitleRow(Context context, TableLayout tableLayout, List<String> columnTitles, Elements peopleList) {
        int colCount = 0;
        Elements firstTitleRows = peopleList.get(0).select("[class=parName]");
        TableRow firstTitleRow = new TableRow(context);
        int specialNumber = 0;
        int extraColSpan = 0;
        for (Element e: firstTitleRows) {
            AppCompatTextView columnTitle = (AppCompatTextView) LayoutInflater.from(context).inflate(R.layout.column_title, null);
            columnTitle.setGravity(Gravity.CENTER);
            String rowSpan = e.attr("rowspan");
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
            if (Utils.isEmpty(rowSpan)) {
                String colSpan = e.attr("colspan");
                try {
                    specialNumber = colCount;
                    extraColSpan = Integer.parseInt(colSpan);
                    layoutParams.span = Integer.parseInt(colSpan);
                } catch (NumberFormatException ne) {
                    layoutParams.span = 1;
                    extraColSpan = 1;
                }
            } else {
                layoutParams.span = 1;
            }

            columnTitles.add(colCount, e.text());
            colCount++;
            columnTitle.setText(e.text());
            columnTitle.setLayoutParams(layoutParams);
            columnTitle.setBackgroundResource(R.color.column_title_color);
            columnTitle.setTextColor(ContextCompat.getColor(context, R.color.row_item_color));
            firstTitleRow.addView(columnTitle);
        }
        tableLayout.addView(firstTitleRow);

        Elements secondTitleRows = peopleList.get(1).select("[class=parName]");
        processSecondTitleRow(context, tableLayout, specialNumber, secondTitleRows, columnTitles, colCount, extraColSpan);

        colCount += extraColSpan;


        int studentsCount = 0;
        for (int i = 2; i < peopleList.size(); i++) {
            Elements fields = peopleList.get(i).select("tr").select("td");
            TableRow preStudentRow = new TableRow(context);
            for (int j = 0; j < fields.size(); j++) {
                AppCompatTextView preStudentView = (AppCompatTextView) LayoutInflater.from(context).inflate(R.layout.column_title, null);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
                layoutParams.span = 1;
                preStudentView.setText(fields.get(j).text());
                preStudentView.setBackgroundResource(R.color.row_item_color);
                preStudentRow.addView(preStudentView);
                studentsCount++;
                if (j == fields.size() - 1) {
                    preStudentRow.requestFocus();
                }
            }
            if (peopleList.size() < colCount) {
                TextView space = new TextView(context);
                TableRow.LayoutParams firstParams = new TableRow.LayoutParams();
                firstParams.span = colCount - peopleList.size();
                space.setLayoutParams(firstParams);
                space.setTextColor(ContextCompat.getColor(context, R.color.row_item_color));
                preStudentRow.addView(space);
            }
            tableLayout.addView(preStudentRow);
        }
        return studentsCount;
    }

    private static void processSecondTitleRow(Context context, TableLayout tableLayout, int specialNumber,
                                              Elements secondTitleRows, List<String> columnTitles, int colCount, int extraColSpan) {
        TableRow secondTitleRow = new TableRow(context);
        TextView firstSpace = new TextView(context);
        TableRow.LayoutParams firstParams = new TableRow.LayoutParams();
        firstParams.span = specialNumber;
        firstSpace.setLayoutParams(firstParams);
        firstSpace.setBackgroundResource(R.color.column_title_color);
        firstSpace.setTextColor(ContextCompat.getColor(context, R.color.row_item_color));
        secondTitleRow.addView(firstSpace);
        for (Element e: secondTitleRows) {
            AppCompatTextView columnTitle = (AppCompatTextView) LayoutInflater.from(context).inflate(R.layout.column_title, null);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
            layoutParams.span = 1;
            columnTitles.add(specialNumber, e.text());
            columnTitle.setText(e.text());
            columnTitle.setLayoutParams(layoutParams);
            columnTitle.setGravity(Gravity.CENTER);
            columnTitle.setBackgroundResource(R.color.column_title_color);
            columnTitle.setTextColor(ContextCompat.getColor(context, R.color.row_item_color));
            secondTitleRow.addView(columnTitle);
        }
        TextView secondSpace = new TextView(context);
        TableRow.LayoutParams secondParams = new TableRow.LayoutParams();
        secondParams.span = colCount - extraColSpan + 1;
        secondSpace.setLayoutParams(secondParams);
        secondSpace.setBackgroundResource(R.color.column_title_color);
        secondSpace.setTextColor(ContextCompat.getColor(context, R.color.row_item_color));
        secondTitleRow.addView(secondSpace);
        tableLayout.addView(secondTitleRow);
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
            Log.e(TAG, "Ошибка при авторизации: ", e);
        }
    }

    public static void checkAuth(String checkQuery, Callback<Boolean> callback) {
        try {
            String result = new CheckAuthTask(callback).execute(checkQuery).get();
            Log.d(TAG, "Запрос проверки авторизации прошел успешно: " + result);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Ошибка при проверке авторизации:", e);
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
