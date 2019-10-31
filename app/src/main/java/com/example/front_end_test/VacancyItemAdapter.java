package com.example.front_end_test;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.front_end_test.R;
import com.example.front_end_test.VacancyPageActivity;
import com.example.front_end_test.VacansyItem;

import java.util.ArrayList;

public class VacancyItemAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<VacansyItem> objects;

    VacancyItemAdapter(Context context, int resource, ArrayList<VacansyItem> items) {
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
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final VacansyItem Item = objects.get(position);

        viewHolder.vacancy_name.setText(Item.getVacancy_name());
        viewHolder.tasks.setText(Item.getTasks());
        viewHolder.requirements.setText(Item.getRequirements());
        viewHolder.payment.setText(Item.getPayment());
        viewHolder.address.setText(Item.getAddress());
        viewHolder.company.setText(Item.getCompany());

        final View finalView = convertView;

        viewHolder.SingleItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(finalView.getContext(), VacancyPageActivity.class);
                intent.putExtra("Link", Item.getLink());
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                finalView.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        final TextView vacancy_name, tasks, requirements, payment, address, company;
        final ConstraintLayout SingleItemLayout;

        ViewHolder(View view) {
            vacancy_name = view.findViewById(R.id.vacancy_name);
            tasks = view.findViewById(R.id.tasks);
            requirements = view.findViewById(R.id.requirements);
            payment = view.findViewById(R.id.payment);
            address = view.findViewById(R.id.address);
            company = view.findViewById(R.id.company);
            SingleItemLayout = view.findViewById(R.id.SingleItemLayout);
        }
    }
}