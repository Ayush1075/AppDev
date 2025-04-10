package com.example.signIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private TextView tvWelcome;
    private TextView tvUserName;
    private ImageView ivUserPhoto;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserName = findViewById(R.id.tvUserName);
        ivUserPhoto = findViewById(R.id.ivUserPhoto);
        btnLogout = findViewById(R.id.btnLogout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set logout button click listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        // Display user information
        displayUserInfo();
    }

    private void displayUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Update UI with user data
            String name = user.getDisplayName();
            String email = user.getEmail();

            tvWelcome.setText("Welcome to Home Screen!");
            tvUserName.setText(name != null ? name : email);

            // Load user profile picture if available (without Glide)
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                try {
                    // Without Glide, we can set the image URI directly
                    // Note: This basic approach may not work for all image types or URLs
                    ivUserPhoto.setImageURI(photoUrl);
                } catch (Exception e) {
                    // If image loading fails, hide the ImageView or use a default image
                    ivUserPhoto.setVisibility(View.GONE);
                    // Alternatively, set a default image
                    // ivUserPhoto.setImageResource(R.drawable.default_profile);
                }
            } else {
                // No profile image available
                ivUserPhoto.setVisibility(View.GONE);
                // Or set a default image
                // ivUserPhoto.setImageResource(R.drawable.default_profile);
            }
        } else {
            // User not logged in, return to login screen
            goToLoginScreen();
        }
    }

    private void signOut() {
        // Sign out from Firebase
        mAuth.signOut();

        // Sign out from Google
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                goToLoginScreen();
            }
        });
    }

    private void goToLoginScreen() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }
}