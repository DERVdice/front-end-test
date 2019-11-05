package com.example.front_end_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences app_settings;
    private SharedPreferences.Editor settings_editor;

    private int current_vacansy_count_for_download;
    private int max_vacansy_count_for_download;

    private SeekBar seekBar;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        app_settings = getSharedPreferences("settings", MODE_PRIVATE);
        settings_editor = app_settings.edit();

        current_vacansy_count_for_download = app_settings.getInt("current_count", 50);
        max_vacansy_count_for_download = app_settings.getInt("max_count", 50);

        seekBar = findViewById(R.id.seekBar2);
        seekBar.setMax(max_vacansy_count_for_download);
        seekBar.setProgress(current_vacansy_count_for_download);
        seekBar.setMin(1);

        BottomNavigationView Top_menu = findViewById(R.id.topNavigationView);
        Top_menu.setOnNavigationItemSelectedListener(top_navigation_menu);

        final TextView seekbar_counter_text = findViewById(R.id.counter_settings);
        seekbar_counter_text.setText(String.valueOf(current_vacansy_count_for_download));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar_counter_text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekbar_counter_text.setText(String.valueOf(seekBar.getProgress()));
                settings_editor.putInt("current_count", seekBar.getProgress());
                settings_editor.apply();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener top_navigation_menu = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.return_back:
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
            }
            return true;
        }
    };

}
