package com.pkmpei.mobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;
import com.pkmpei.mobile.News;
import java.util.LinkedList;
import java.util.List;

public class NewsTitleAdapter extends BaseAdapter {

    private Context context;
    private List<News> newsList = new LinkedList<>();

    public NewsTitleAdapter(Context context, List<News> newsList){
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.news_title_item, null);
        }

        if (convertView == null) {
            return null;
        }

        TextView newsTitle = (TextView) convertView.findViewById(R.id.news_title_view);
        newsTitle.setText(newsList.get(position).getTitle());

        TextView newsDate = (TextView) convertView.findViewById(R.id.news_date_view);
        newsDate.setText(newsList.get(position).getDate());
        if (newsList.get(position).isExpandable()) {
            newsDate.setTextColor(ContextCompat.getColor(context, R.color.color_news_date_expandable));
            newsTitle.setTextColor(ContextCompat.getColor(context, R.color.color_news_title_expandable));
            SpannableString content = new SpannableString(newsTitle.getText() + "...");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            newsTitle.setText(content);
        } else {
            newsDate.setTextColor(ContextCompat.getColor(context, R.color.color_news_date_regular));
            newsTitle.setTextColor(ContextCompat.getColor(context, R.color.color_news_title_regular));
        }

        convertView.setClickable(!newsList.get(position).isExpandable());

        return convertView;
    }
}