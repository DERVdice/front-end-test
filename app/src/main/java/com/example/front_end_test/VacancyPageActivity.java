package com.example.front_end_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VacancyPageActivity extends AppCompatActivity {

    private WebView webView;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_page);

        progressBar = findViewById(R.id.progressBar2);

        Bundle Data_From_Last_Activity = getIntent().getExtras();
        String url = Data_From_Last_Activity.getString("Link");

        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        BottomNavigationView Top_menu = findViewById(R.id.topNavigationView);
        Top_menu.setOnNavigationItemSelectedListener(top_navigation_menu);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener top_navigation_menu = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.return_back:
                    Intent intent = new Intent(VacancyPageActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
            }
            return true;
        }
    };
}
