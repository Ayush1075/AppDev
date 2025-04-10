package com.example.q3;

import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private Button playButton;
    private Button pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        animationView = findViewById(R.id.animationView);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);

        // Set up button click listeners
        playButton.setOnClickListener(v -> animationView.playAnimation());
        pauseButton.setOnClickListener(v -> animationView.pauseAnimation());

        // Optional config
        animationView.setSpeed(1.0f);
        animationView.setRepeatCount(-1);
    }
}
