package com.example.front_end_test;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class StatisticActivityListOneItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<StatisticActivityListOneItem> objects;

    StatisticActivityListOneItemAdapter(Context context, int resource, ArrayList<StatisticActivityListOneItem> items) {
        this.layout = resource;
        this.objects = items;
        this.inflater = LayoutInflater.from(context);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final StatisticActivityListOneItemAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new StatisticActivityListOneItemAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StatisticActivityListOneItemAdapter.ViewHolder) convertView.getTag();
        }

        final StatisticActivityListOneItem Item = objects.get(position);

        viewHolder.company.setText(Item.getCompany());
        viewHolder.count.setText(Item.getCount());

        return convertView;
    }

    private class ViewHolder {
        final TextView company, count;

        ViewHolder(View view) {
            company = view.findViewById(R.id.company);
            count = view.findViewById(R.id.count);
        }
    }
}
