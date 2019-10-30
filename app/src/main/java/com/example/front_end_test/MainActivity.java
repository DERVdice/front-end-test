package com.example.front_end_test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView main_list;

    // Страница с вакансиями делится на 3 блока: верхний и нижний - оплаченные объявления, средний - обычные
    //private Elements top_block;
    private Elements middle_block;
    //private Elements bottom_block;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        main_list = findViewById(R.id.main_list);
        new Parse().execute();

        arrayAdapter = new ArrayAdapter<>(this, R.layout.main_list_item_layout, R.id.test_text, arrayList);

    }


    public class Parse extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg) {
            Document doc;
            try {
                // Спарсили html страницу в документ
                doc = Jsoup.connect("https://kaluga.hh.ru/search/vacancy?area=43&clusters=true&enable_snippets=true&no_magic=true&specialization=1.221&from=cluster_specialization&showClusters=true")
                        .userAgent("Mozilla/5.0 (X11;Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0")
                        .referrer("http://www.google.com")
                        .get();

                // Вытащили блоки, содержащие вакансии
                middle_block = doc.select(".vacancy-serp-item");

                for (Element element : middle_block) {   //.select(".g-user-content")
                    arrayList.add(element.select(".resume-search-item__name").text());                                   // Вакансия
                    arrayList.add(element.select(".vacancy-serp-item__compensation").text());                            // Зарплата
                    arrayList.add(element.select(".bloko-link_secondary").text());                                       // Компания
                    arrayList.add(element.select("[data-qa=vacancy-serp__vacancy_snippet_responsibility]").text());      // Задачи
                    arrayList.add(element.select("[data-qa=vacancy-serp__vacancy_snippet_requirement]").text());         // Требования
                    arrayList.add(element.select("[data-qa=vacancy-serp__vacancy-address]").text());                     // Город
                    arrayList.add(element.select("[data-qa=vacancy-serp__vacancy-title]").attr("href"));      // Ссылка на страницу вакансии
                    arrayList.add("-----------------------------------------------");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String Result) {
            main_list.setAdapter(arrayAdapter);
        }
    }
}


