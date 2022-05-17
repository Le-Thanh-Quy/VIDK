package com.quy.hc05;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListHistory  extends BaseAdapter {
    private List<History> listData;
    private LayoutInflater layoutInflater;
    private Context context;
    public ListHistory(Context aContext, List<History> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = layoutInflater.inflate(R.layout.item_list, parent, false);
        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.temperature = (TextView) convertView.findViewById(R.id.temperature);
        holder.humidity = (TextView) convertView.findViewById(R.id.humidity);

        History history = listData.get(position);

        holder.time.setText(history.time);
        holder.temperature.setText(history.temp);
        holder.humidity.setText(history.humidity);

        return convertView;
    }


    static class ViewHolder {
        TextView time;
        TextView temperature;
        TextView humidity;
    }
}
