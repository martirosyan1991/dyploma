package com.pkmpei.mobile.adapter;

import com.pkmpei.mobile.model.MenuItem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MenuItem> menuItems;

    public NavDrawerListAdapter(Context context, ArrayList<MenuItem> menuItems){
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
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
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        txtTitle.setTextColor(0xffffffff);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        imgIcon.setImageResource(menuItems.get(position).getIcon());
        txtTitle.setText(menuItems.get(position).getTitle());

        // displaying count
        // check whether it set visible or not
        if(menuItems.get(position).getCounterVisibility()){
            txtCount.setText(menuItems.get(position).getCount());
        }else{
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }

        return convertView;
    }

}