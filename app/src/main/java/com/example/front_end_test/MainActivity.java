package com.example.front_end_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences app_settings;
    private SharedPreferences.Editor settings_editor;

    private ArrayList<VacansyItem> arrayList = new ArrayList<>();
    private VacancyItemAdapter arrayAdapter;
    private ListView main_list;
    private double count = 50;
    private int current_vacansy_count_for_download;
    private int max_vacansy_count_for_download;
    private Button search_button;
    private EditText search_box;
    private Boolean search_flag = false;
    private static ProgressBar progressBar;
    private BottomNavigationView Bot_Menu;
    private boolean bottom_menu_enabled = false;
    private String header;

    // Страница с вакансиями делится на 3 блока: верхний и нижний - оплаченные объявления, средний - обычные
    private Elements middle_block;

    @Override
    protected void onResume() {
        super.onResume();
        app_settings = getSharedPreferences("settings", MODE_PRIVATE);
        settings_editor = app_settings.edit();
        current_vacansy_count_for_download = app_settings.getInt("current_count", 50);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        app_settings = getSharedPreferences("settings", MODE_PRIVATE);
        settings_editor = app_settings.edit();
        current_vacansy_count_for_download = app_settings.getInt("current_count", 50);

        Bot_Menu = findViewById(R.id.bottomNavigationView);
        progressBar = findViewById(R.id.progressBar);

        main_list = findViewById(R.id.main_list);
        new Parse().execute();

        arrayAdapter = new VacancyItemAdapter(this, R.layout.main_list_item_layout, arrayList);

        search_box = findViewById(R.id.search_field);
        search_button = findViewById(R.id.search_button);

        Bot_Menu.setOnNavigationItemSelectedListener(bot_menu_listener);

        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search_flag = false;
                search_button.setBackground(MainActivity.this.getDrawable(R.drawable.ic_search_black_24dp));
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bot_menu_listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (bottom_menu_enabled) {
                switch (menuItem.getItemId()) {
                    case R.id.settings_btn:
                        Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent2);
                        break;

                    case R.id.refresh_btn:
                        arrayList.clear();
                        arrayAdapter.notifyDataSetChanged();
                        new Parse().execute();
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

                            Intent intent = new Intent(MainActivity.this, StatisticActivity.class);

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
        if ((search_flag == false) && (!search_box.getText().toString().equals(""))) {
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
                search_flag = true;
            }
            //search_flag = true;
        } else {
            search_button.setBackground(MainActivity.this.getDrawable(R.drawable.ic_search_black_24dp));
            search_box.setText("");
            arrayAdapter = new VacancyItemAdapter(this, R.layout.main_list_item_layout, arrayList);
            main_list.setAdapter(arrayAdapter);
            search_flag = false;
        }
    }


    public class Parse extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg) {
            Document doc;
            long counter = Math.round(count);

            try {
                String url = "https://kaluga.hh.ru/search/vacancy?L_is_autosearch=false&area=43&clusters=true&enable_snippets=true&no_magic=true&specialization=1.221&page=0";
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (X11;Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0")
                        .referrer("http://www.google.com")
                        .get();
                header = doc.select("[data-qa=page-title]").text();
                max_vacansy_count_for_download = Integer.valueOf(header.substring(header.indexOf("о") + 2, header.indexOf("в") - 1));

                settings_editor.putInt("max_count", max_vacansy_count_for_download);
                settings_editor.apply();

                if (current_vacansy_count_for_download > max_vacansy_count_for_download) {
                    current_vacansy_count_for_download = max_vacansy_count_for_download;
                    settings_editor.putInt("current_count", current_vacansy_count_for_download);
                    settings_editor.apply();
                }

                count = (double) current_vacansy_count_for_download;
                counter = Math.round(count);

            } catch (Exception e) {
                e.printStackTrace();
            }


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
                        header = doc.select("[data-qa=page-title]").text();

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
            progressBar.setVisibility(View.INVISIBLE);
            bottom_menu_enabled = true;

            Toast.makeText(MainActivity.this, "Вакансий загружено: " + current_vacansy_count_for_download, Toast.LENGTH_LONG).show();
            //Bot_Menu.setEnabled(true);
            //Bot_Menu.setClickable(true);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //Bot_Menu.setEnabled(false);
            //Bot_Menu.setClickable(false);
            bottom_menu_enabled = false;
        }
    }

}


