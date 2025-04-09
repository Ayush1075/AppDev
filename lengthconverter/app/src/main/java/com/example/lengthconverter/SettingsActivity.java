package com.example.lengthconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Settings Activity for managing application preferences
 * Currently handles theme switching between light and dark mode
 */
public class SettingsActivity extends AppCompatActivity {

    // UI Components
    private Switch switchTheme;
    private Button buttonApply;

    // Shared Preferences for storing settings
    private SharedPreferences sharedPreferences;
    private static final String PREFERENCES_NAME = "unit_converter_prefs";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        switchTheme = findViewById(R.id.switch_theme);
        buttonApply = findViewById(R.id.button_apply_settings);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        // Load current settings
        boolean isDarkModeOn = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        switchTheme.setChecked(isDarkModeOn);

        // Set click listener for apply button
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                applySettings();

                // Return to main activity
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Save the current settings to SharedPreferences
     */
    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DARK_MODE, switchTheme.isChecked());
        editor.apply();
    }

    /**
     * Apply the settings immediately
     */
    private void applySettings() {
        boolean isDarkModeOn = switchTheme.isChecked();

        // Set the appropriate theme
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}