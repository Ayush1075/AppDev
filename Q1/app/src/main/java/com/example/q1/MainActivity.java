package com.example.q1;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText inputValue;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Button buttonConvert;
    private TextView textResult;

    // Conversion rates to meters (base unit)
    private static final double FEET_TO_METERS = 0.3048;
    private static final double INCHES_TO_METERS = 0.0254;
    private static final double CM_TO_METERS = 0.01;
    private static final double YARDS_TO_METERS = 0.9144;

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

        // Setup spinners with length units
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.length_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Set convert button click listener
        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertUnits();
            }
        });
    }

    private void convertUnits() {
        // Get user input
        String valueStr = inputValue.getText().toString();
        if (valueStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double value = Double.parseDouble(valueStr);
            int fromUnitPosition = spinnerFrom.getSelectedItemPosition();
            int toUnitPosition = spinnerTo.getSelectedItemPosition();

            // Convert to meters first (base unit)
            double valueInMeters = convertToMeters(value, fromUnitPosition);

            // Then convert from meters to target unit
            double result = convertFromMeters(valueInMeters, toUnitPosition);

            // Format and display the result
            DecimalFormat df = new DecimalFormat("#.#####");
            String unitFrom = getResources().getStringArray(R.array.length_units)[fromUnitPosition];
            String unitTo = getResources().getStringArray(R.array.length_units)[toUnitPosition];

            textResult.setText(df.format(value) + " " + unitFrom + " = " +
                    df.format(result) + " " + unitTo);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertToMeters(double value, int fromUnit) {
        switch (fromUnit) {
            case 0: // Feet
                return value * FEET_TO_METERS;
            case 1: // Inches
                return value * INCHES_TO_METERS;
            case 2: // Centimeters
                return value * CM_TO_METERS;
            case 3: // Meters
                return value; // Already in meters
            case 4: // Yards
                return value * YARDS_TO_METERS;
            default:
                return value;
        }
    }

    private double convertFromMeters(double valueInMeters, int toUnit) {
        switch (toUnit) {
            case 0: // Feet
                return valueInMeters / FEET_TO_METERS;
            case 1: // Inches
                return valueInMeters / INCHES_TO_METERS;
            case 2: // Centimeters
                return valueInMeters / CM_TO_METERS;
            case 3: // Meters
                return valueInMeters; // Already in meters
            case 4: // Yards
                return valueInMeters / YARDS_TO_METERS;
            default:
                return valueInMeters;
        }
    }
}