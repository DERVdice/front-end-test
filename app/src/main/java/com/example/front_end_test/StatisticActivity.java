package com.example.front_end_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class StatisticActivity extends AppCompatActivity {

    private double count = 50;  // Количество объявлений

    private ArrayList<String> linksList;
    private ArrayList<String> vacancyList;
    private ArrayList<String> companyList;
    private ArrayList<String> addressList;
    private ArrayList<String> paymentList;

    String temp = "";   // Тестовая переменная для вывода навыков на экран

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
/*
        Функции бд было решено не добавлять, но и написанный код на всякий случай удалять не стал

        SQLiteDatabase db = getApplicationContext().openOrCreateDatabase("save.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + "search_results" + " (vacancy_name TEXT, payment TEXT," +
                "company TEXT,  address TEXT,  link TEXT PRIMARY KEY)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + "skills" + " (link TEXT, skills TEXT)");
        db.close();
*/
        Bundle Data_From_Last_Activity = getIntent().getExtras();
        assert Data_From_Last_Activity != null;

        linksList = Data_From_Last_Activity.getStringArrayList("linksList");
        vacancyList = Data_From_Last_Activity.getStringArrayList("vacancyList");
        companyList = Data_From_Last_Activity.getStringArrayList("companyList");
        addressList = Data_From_Last_Activity.getStringArrayList("addressList");
        paymentList = Data_From_Last_Activity.getStringArrayList("paymentList");

        //Full_DataBase(linksList.size());

        Make_Graph_1();
        Make_Graph_2();

        BottomNavigationView Top_menu = findViewById(R.id.topNavigationView);
        Top_menu.setOnNavigationItemSelectedListener(top_navigation_menu);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener top_navigation_menu = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.return_back:
                    Intent intent = new Intent(StatisticActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
            }
            return true;
        }
    };


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Make_Graph_1() {
        // Построение списка "количество объявлений по работодателям"
        TextView counter_Text = findViewById(R.id.counter_1);
        TextView company_Text = findViewById(R.id.company_1);

        // Закинули все в hashMap, где Key=Компания Value=Количество объявлений
        Map<String, Integer> companyDictionary = new HashMap<>();
        for (int i = 0; i < companyList.size(); i++) {
            if (companyDictionary.containsKey(companyList.get(i))) {
                int counter = Integer.valueOf(companyDictionary.get(companyList.get(i))) + 1;
                companyDictionary.put(companyList.get(i), counter);
            } else {
                companyDictionary.put(companyList.get(i), 1);
            }
        }

        // Сортировка по убыванию
        Map<String, Integer> sorted = companyDictionary.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(
                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));


        // Заполняем для адаптера
        Iterator it = sorted.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            counter_Text.setText(counter_Text.getText().toString() + pair.getValue().toString() + '\n');
            company_Text.setText(company_Text.getText().toString() + pair.getKey().toString() + '\n');
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean Switch_View_Scale(View scale_view) {
        if (scale_view.getVisibility() == View.GONE) {
            scale_view.setVisibility(View.VISIBLE);
            // Место для анимации
            return true;
        } else {
            // Место для анимации
            scale_view.setVisibility(View.GONE);
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void item_1_click(View v) {
        View temp = findViewById(R.id.list_frame);
        boolean stage = Switch_View_Scale(temp);    // Переключается видимость элемента

        TextView textView = (TextView) v;
        if (stage) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void item_2_click(View v) {
        View temp = findViewById(R.id.graph_1_frame);
        boolean stage = Switch_View_Scale(temp);    // Переключается видимость элемента

        TextView textView = (TextView) v;
        if (stage) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
        }
    }

    private int Convert_Payment(String string_payment, Boolean lowest_or_highest_payment) {
        if (string_payment.equals("")) {
            return -10000;
        }
        if (string_payment.charAt(0) != 'о') {
            if (lowest_or_highest_payment) {
                return Integer.valueOf(string_payment.substring(string_payment.indexOf("-") + 1, string_payment.length() - 5).replace(" ", ""));
            } else {
                return Integer.valueOf(string_payment.substring(0, string_payment.indexOf("-")).replace(" ", ""));
            }
        } else {
            return Integer.valueOf(string_payment.substring(3, string_payment.length() - 5).replace(" ", ""));
        }
    }

    @SuppressLint("SetTextI18n")
    private void Make_Graph_2() {
        // График количества объявлений по компаниям
        GraphView graph = findViewById(R.id.graph_1);
        TextView counter_Text = findViewById(R.id.counter_2);
        TextView company_Text = findViewById(R.id.company_2);

        // Заполнение списка минимальных зарплат у работодателя
        Map<String, Integer> minPriceMap = new HashMap<>();
        for (int i = 0; i < companyList.size(); i++) {

            int price = Convert_Payment(paymentList.get(i), false);

            // если элемент уже есть в Map
            if (minPriceMap.containsKey(companyList.get(i))) {
                // если значение в Map больше, то заменяем его текущим price
                if (minPriceMap.get(companyList.get(i)) > price)
                    minPriceMap.put(companyList.get(i), price);
            }
            // если ключ встречается впервые
            else {
                minPriceMap.put(companyList.get(i), price);
            }
        }


        BarGraphSeries<DataPoint> series_min = new BarGraphSeries<>();
        int x = 0;
        Iterator it = minPriceMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            x++;
            int y = Integer.valueOf(pair.getValue().toString());
            series_min.appendData(new DataPoint(x, y), true, minPriceMap.size() + 1);

            counter_Text.setText(counter_Text.getText().toString() + x + '\n');
            company_Text.setText(company_Text.getText().toString() + pair.getKey().toString() + '\n');
        }

        series_min.setSpacing(30);
        series_min.setColor(R.color.colorPrimary);

        // Заполнение списка максимальных зарплат у работодателя
        Map<String, Integer> maxPriceMap = new HashMap<>();
        for (int i = 0; i < companyList.size(); i++) {

            int price = Convert_Payment(paymentList.get(i), true);

            // если элемент уже есть в Map
            if (maxPriceMap.containsKey(companyList.get(i))) {
                // если значение в Map больше, то заменяем его текущим price
                if (maxPriceMap.get(companyList.get(i)) < price)
                    maxPriceMap.put(companyList.get(i), price);
            }
            // если ключ встречается впервые
            else {
                maxPriceMap.put(companyList.get(i), price);
            }
        }

        BarGraphSeries<DataPoint> series_max = new BarGraphSeries<>();
        int x1 = 0;
        Iterator it1 = maxPriceMap.entrySet().iterator();
        while (it1.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it1.next();
            x1++;
            int y = Integer.valueOf(pair.getValue().toString());

            series_max.appendData(new DataPoint(x1, y), true, maxPriceMap.size() + 1);
        }

        series_max.setSpacing(30);
        series_max.setColor(R.color.Material_gray);

        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(series_min.getLowestValueY());
        graph.getViewport().setMaxY(series_max.getHighestValueY());

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(minPriceMap.size() + 1);

        // enable scaling and scrolling
        graph.getViewport().setScrollable(true);
        //graph.getViewport().setScalable(true);
        //graph.getViewport().setScalableY(true);
        graph.addSeries(series_min);
        graph.addSeries(series_max);

        graph.getGridLabelRenderer().setHorizontalLabelsColor(R.color.Material_gray);
        graph.getGridLabelRenderer().setVerticalLabelsColor(R.color.Material_gray);
        graph.getGridLabelRenderer().setTextSize(30);
    }


/*
    Функции бд было решено не добавлять, но и написанный код на всякий случай удалять не стал

    private void Full_DataBase(int item_count) {
        SQLiteDatabase db = getApplicationContext().openOrCreateDatabase("save.db", MODE_PRIVATE, null);
        db.execSQL("DROP TABLE search_results");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + "search_results" + " (vacancy_name TEXT, payment TEXT," +
                "company TEXT,  address TEXT,  link TEXT PRIMARY KEY)");

        for (int i = 0; i < item_count; i++) {
            ContentValues Val = new ContentValues();
            Val.put("vacancy_name", vacancyList.get(i));
            Val.put("payment", paymentList.get(i));
            Val.put("company", companyList.get(i));
            Val.put("address", addressList.get(i));
            Val.put("link", linksList.get(i));
            db.beginTransaction();
            db.insert("search_results", null, Val);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        db.close();
    }
 */
/*
    //
    public class Parse2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg) {

            //SQLiteDatabase db = getApplicationContext().openOrCreateDatabase("save.db", MODE_PRIVATE, null);
            //db.execSQL("DROP TABLE skills");
            //db.execSQL("CREATE TABLE IF NOT EXISTS " + "skills" + " (link TEXT, skills TEXT)");

            Document doc;

            //for (int i = 0; i < linksList.size(); i++) {
            try {
                String url = linksList.get(1);
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (X11;Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0")
                        .referrer("http://www.google.com")
                        .get();

                Elements skills_block = doc.select("[data-qa=skills-element]");
                for (Element element : skills_block) {
                    temp += " " + element.text();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            //}
            return null;

        }

        @Override
        protected void onPostExecute(String Result) {
            Toast.makeText(StatisticActivity.this, temp, Toast.LENGTH_LONG).show();
        }
    }

 */
}
