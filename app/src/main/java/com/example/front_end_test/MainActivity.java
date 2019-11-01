package com.example.front_end_test;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<VacansyItem> arrayList = new ArrayList<>();
    private VacancyItemAdapter arrayAdapter;
    private ListView main_list;
    private double count = 50;
    private Button search_button;
    private EditText search_box;
    private Boolean search_flag = false;
    private Boolean thread_flag = false;
    private static ProgressBar progressBar;

    // Страница с вакансиями делится на 3 блока: верхний и нижний - оплаченные объявления, средний - обычные
    private Elements middle_block;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        progressBar = findViewById(R.id.progressBar2);
        progressBar.setMax((int) count);

        main_list = findViewById(R.id.main_list);
        new Parse().execute();

        arrayAdapter = new VacancyItemAdapter(this, R.layout.main_list_item_layout, arrayList);

        search_box = findViewById(R.id.search_field);
        search_button = findViewById(R.id.search_button);

        BottomNavigationView Bot_Menu = findViewById(R.id.bottomNavigationView);
        Bot_Menu.setOnNavigationItemSelectedListener(bot_menu_listener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bot_menu_listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.settings_btn:

                    break;

                case R.id.refresh_btn:
                    if (!thread_flag) { // Защита от нескольких потоков
                        thread_flag = true;
                        arrayList.clear();
                        new Parse().execute();
                    }
                    break;

                case R.id.graps_btn:
                    if (!arrayList.isEmpty()) {
                        // Формируется и передается на новую Activity список, содержащий ссылки на все вакансии из arrayList
                        // Так же передаются те данные, которые нет смысла парсить повторно (для экономии трафика)
                        ArrayList<String> linksList = new ArrayList<>();
                        ArrayList<String> vacancyList = new ArrayList<>();
                        ArrayList<String> companyList = new ArrayList<>();
                        ArrayList<String> addressList = new ArrayList<>();
                        ArrayList<String> paymentList = new ArrayList<>();

                        for (int i = 0; i < arrayList.size(); i++) {
                            linksList.add(arrayList.get(i).getLink());
                            vacancyList.add(arrayList.get(i).getVacancy_name());
                            companyList.add(arrayList.get(i).getCompany());
                            addressList.add(arrayList.get(i).getAddress());
                            paymentList.add(arrayList.get(i).getPayment());
                        }

                        Intent intent = new Intent(MainActivity.this, GraphsActivity.class);

                        intent.putExtra("linksList", linksList);
                        intent.putExtra("vacancyList", vacancyList);
                        intent.putExtra("companyList", companyList);
                        intent.putExtra("addressList", addressList);
                        intent.putExtra("paymentList", paymentList);

                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Не хватает данных для построения графиков", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
            return true;
        }
    };


    @Override
    public void onBackPressed() {
        if (search_box.isFocused()) {
            search_box.clearFocus();
        } else {
            finish();
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void Search(View v) {
        if (search_flag == false) {
            search_button.setBackground(MainActivity.this.getDrawable(R.drawable.ic_close_black_24dp));
            ArrayList<VacansyItem> search_arrayList = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getVacancy_name().contains(search_box.getText().toString())) {
                    search_arrayList.add(arrayList.get(i));
                }
            }
            if (search_arrayList.isEmpty()) {
                Toast.makeText(this, "Совпадений не найдено", Toast.LENGTH_LONG).show();
                search_button.setBackground(MainActivity.this.getDrawable(R.drawable.ic_search_black_24dp));
                arrayAdapter = new VacancyItemAdapter(this, R.layout.main_list_item_layout, arrayList);
                main_list.setAdapter(arrayAdapter);
                search_flag = false;
            } else {
                arrayAdapter = new VacancyItemAdapter(this, R.layout.main_list_item_layout, search_arrayList);
                main_list.setAdapter(arrayAdapter);
            }
            search_flag = true;
        } else {
            search_button.setBackground(MainActivity.this.getDrawable(R.drawable.ic_search_black_24dp));
            search_box.setText("");
            arrayAdapter = new VacancyItemAdapter(this, R.layout.main_list_item_layout, arrayList);
            main_list.setAdapter(arrayAdapter);
            search_flag = false;
        }
    }

    private static void Update_ProgressBar(int count) {
        progressBar.incrementProgressBy(1);
        if (progressBar.getProgress() == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (progressBar.getProgress() == count) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
        }
    }

/*
    private static void Refresh_ProgressBar(){
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
    }

 */

    public class Parse extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg) {
            Document doc;
            //Refresh_ProgressBar();
            long counter = Math.round(count);
            while (counter > 0) {
                for (int i = 0; i < Math.round(Math.ceil(count / 20)); i++) {
                    try {
                        // Спарсили html страницу в документ
                        String url = "https://kaluga.hh.ru/search/vacancy?L_is_autosearch=false&area=43&clusters=true&enable_snippets=true&no_magic=true&specialization=1.221&page=" + i;
                        doc = Jsoup.connect(url)
                                .userAgent("Mozilla/5.0 (X11;Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0")
                                .referrer("http://www.google.com")
                                .get();
                        // Вытащили блоки, содержащие вакансии
                        middle_block = doc.select(".vacancy-serp-item");

                        for (Element element : middle_block) {   //.select(".g-user-content")
                            if (counter > 0) {
                                String t1, t2, t3, t4, t5, t6, t7;
                                t1 = element.select(".resume-search-item__name").text();                                   // Вакансия
                                t2 = element.select(".vacancy-serp-item__compensation").text();                            // Зарплата
                                t3 = element.select(".bloko-link_secondary").text();                                       // Компания
                                t4 = element.select("[data-qa=vacancy-serp__vacancy_snippet_responsibility]").text();      // Задачи
                                t5 = element.select("[data-qa=vacancy-serp__vacancy_snippet_requirement]").text();         // Требования
                                t6 = element.select("[data-qa=vacancy-serp__vacancy-title]").attr("href");      // Ссылка на страницу вакансии
                                t7 = element.select("[data-qa=vacancy-serp__vacancy-address]").text();                     // Город

                                arrayList.add(new VacansyItem(t1, t2, t3, t4, t5, t6, t7));
                            } else break;

                            //Update_ProgressBar((int)count);
                            counter--;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String Result) {
            main_list.setAdapter(arrayAdapter);
            thread_flag = false;
        }
    }

}


