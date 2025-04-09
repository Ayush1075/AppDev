package com.example.lengthconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputValue;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Button buttonConvert;
    private TextView textResult;
    private ImageButton buttonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        inputValue = findViewById(R.id.input_value);
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo = findViewById(R.id.spinner_to);
        buttonConvert = findViewById(R.id.button_convert);
        textResult = findViewById(R.id.text_result);
        buttonSettings = findViewById(R.id.button_settings); // Initialize settings button

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.length_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Convert button click listener
        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertLength();
            }
        });

        // Settings button click listener
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void convertLength() {
        String inputStr = inputValue.getText().toString();
        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double value = Double.parseDouble(inputStr);
            String fromUnit = spinnerFrom.getSelectedItem().toString();
            String toUnit = spinnerTo.getSelectedItem().toString();

            double valueInMeters = convertToMeters(value, fromUnit);
            double result = convertFromMeters(valueInMeters, toUnit);

            String formattedResult = String.format("%.4f %s = %.4f %s",
                    value, fromUnit, result, toUnit);
            textResult.setText(formattedResult);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertToMeters(double value, String unit) {
        switch (unit) {
            case "Meters": return value;
            case "Centimeters": return value / 100;
            case "Inches": return value * 0.0254;
            case "Feet": return value * 0.3048;
            case "Yards": return value * 0.9144;
            case "Kilometers": return value * 1000;
            case "Miles": return value * 1609.34;
            default: return value;
        }
    }

    private double convertFromMeters(double meters, String unit) {
        switch (unit) {
            case "Meters": return meters;
            case "Centimeters": return meters * 100;
            case "Inches": return meters / 0.0254;
            case "Feet": return meters / 0.3048;
            case "Yards": return meters / 0.9144;
            case "Kilometers": return meters / 1000;
            case "Miles": return meters / 1609.34;
            default: return meters;
        }
    }
}
