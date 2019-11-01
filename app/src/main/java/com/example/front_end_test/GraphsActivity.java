package com.example.front_end_test;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class GraphsActivity extends AppCompatActivity {

    private double count = 50;

    private ArrayList<String> linksList;
    private ArrayList<String> vacancyList;
    private ArrayList<String> companyList;
    private ArrayList<String> addressList;
    private ArrayList<String> paymentList;

    private String t1, t2, t3, t4, t5, t6, t7;

    String temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
/*
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

        Full_DataBase(linksList.size());

        BottomNavigationView Bot_Menu = findViewById(R.id.bottomNavigationView2);
        Bot_Menu.setOnNavigationItemSelectedListener(bot_menu_listener2);


        GraphView graph = (GraphView) findViewById(R.id.graph_1);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(5, -1),
                new DataPoint(6, 5),
                new DataPoint(7, 3),
                new DataPoint(8, 2),
                new DataPoint(9, 6)
        });
        graph.setTitle("Уровень зарплаты в разных компаниях");
        graph.addSeries(series);
        graph.addSeries(series);
        graph.setHorizontalScrollBarEnabled(true);
        graph.computeScroll();
    }

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

    private BottomNavigationView.OnNavigationItemSelectedListener bot_menu_listener2 = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.load_graphs:
                    new Parse2().execute();
                    break;
            }
            return true;
        }
    };

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
            Toast.makeText(GraphsActivity.this, temp ,Toast.LENGTH_LONG).show();
        }
    }
}
