package com.pkmpei.mobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;
import com.pkmpei.mobile.News;

import java.util.LinkedList;
import java.util.List;

public class GroupTitleAdapter extends BaseAdapter {

    private Context context;
    private List<String> titles = new LinkedList<>();

    public GroupTitleAdapter(Context context, List<String> titles){
        this.context = context;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
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
            convertView = mInflater.inflate(R.layout.group_title_item, null);
        }

        if (convertView == null || position > titles.size() - 1) {
            return null;
        }

        ((TextView) convertView).setText(titles.get(position));
        return convertView;
    }
}