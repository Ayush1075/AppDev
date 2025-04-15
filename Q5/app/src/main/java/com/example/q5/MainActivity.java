package com.example.q5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS;

    // Define permissions based on Android version
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            REQUIRED_PERMISSIONS = new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            REQUIRED_PERMISSIONS = new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else {
            // Android 10 and below
            REQUIRED_PERMISSIONS = new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    // Enum to track which action to perform after permissions are granted
    private enum PendingAction {
        NONE,
        OPEN_CAMERA,
        OPEN_GALLERY
    }

    private PendingAction pendingAction = PendingAction.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find UI components
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnBrowseGallery = findViewById(R.id.btnBrowseGallery);

        // Handle "Take Photo" button click
        btnTakePhoto.setOnClickListener(view -> {
            pendingAction = PendingAction.OPEN_CAMERA;
            requestPermissionsIfNeeded();
        });

        // Handle "Browse Gallery" button click
        btnBrowseGallery.setOnClickListener(view -> {
            pendingAction = PendingAction.OPEN_GALLERY;
            requestPermissionsIfNeeded();
        });
    }

    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissionsIfNeeded() {
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        } else {
            // Permissions already granted, proceed with the pending action
            processPendingAction();
        }
    }

    private void processPendingAction() {
        switch (pendingAction) {
            case OPEN_CAMERA:
                openCamera();
                break;
            case OPEN_GALLERY:
                openGallery();
                break;
            default:
                // Do nothing
                break;
        }
        // Reset pending action
        pendingAction = PendingAction.NONE;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (allPermissionsGranted(grantResults)) {
                // All permissions granted, proceed with the pending action
                processPendingAction();
            } else {
                Toast.makeText(this, "Permissions not granted. Some features may not work properly.",
                        Toast.LENGTH_LONG).show();
                // Show dialog to guide user to settings
                showPermissionExplanationDialog();
            }
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        // If there are no results, permissions were not granted
        if (grantResults.length == 0) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs camera and storage permissions to function properly. Please grant these permissions in Settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Open app settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openCamera() {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(MainActivity.this, FolderSelectionActivity.class);
        startActivity(intent);
    }
}