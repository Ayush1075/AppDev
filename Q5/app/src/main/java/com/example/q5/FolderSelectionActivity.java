package com.example.q5;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderSelectionActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvCurrentPath;
    private File currentDirectory;
    private List<String> fileList = new ArrayList<>();
    private List<File> files = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_selection);

        listView = findViewById(R.id.listView);
        tvCurrentPath = findViewById(R.id.tvCurrentPath);

        // Choose the appropriate directory to start with
        setupInitialDirectory();
        displayFiles();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File selectedFile = files.get(position);
                if (selectedFile.isDirectory() && selectedFile.canRead()) {
                    // If directory, navigate into it
                    currentDirectory = selectedFile;
                    displayFiles();
                } else if (!selectedFile.canRead()) {
                    Toast.makeText(FolderSelectionActivity.this,
                            "Cannot access this folder due to permission restrictions",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add long press to select folder
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File selectedFile = files.get(position);
                if (selectedFile.isDirectory() && selectedFile.canRead()) {
                    // Open GalleryActivity with the selected folder path
                    Intent intent = new Intent(FolderSelectionActivity.this, GalleryActivity.class);
                    intent.putExtra("folderPath", selectedFile.getAbsolutePath());
                    startActivity(intent);
                    return true;
                } else if (!selectedFile.canRead()) {
                    Toast.makeText(FolderSelectionActivity.this,
                            "Cannot access this folder due to permission restrictions",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void setupInitialDirectory() {
        // On Android 10+ (API 29+), access to external storage is restricted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use app-specific directory
            currentDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (currentDirectory == null) {
                currentDirectory = getFilesDir();
            }
        } else {
            // Try external storage for lower API levels
            File externalDir = Environment.getExternalStorageDirectory();
            if (externalDir != null && externalDir.canRead()) {
                currentDirectory = externalDir;
            } else {
                // Fallback to app-specific directory
                currentDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (currentDirectory == null) {
                    currentDirectory = getFilesDir();
                }
            }
        }
    }

    private void displayFiles() {
        fileList.clear();
        files.clear();

        tvCurrentPath.setText("Current Path: " + currentDirectory.getAbsolutePath());

        // Add parent directory option if not at root
        if (currentDirectory.getParentFile() != null && currentDirectory.getParentFile().canRead()) {
            fileList.add("../");
            files.add(currentDirectory.getParentFile());
        }

        // Get all directories
        File[] fileArray = currentDirectory.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file.isDirectory() && !file.isHidden()) {
                    fileList.add(file.getName() + "/");
                    files.add(file);
                }
            }
        }

        if (fileList.isEmpty()) {
            fileList.add("No accessible folders found");
            Toast.makeText(this, "No accessible folders in this location", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (currentDirectory.getParentFile() != null && currentDirectory.getParentFile().canRead()) {
            currentDirectory = currentDirectory.getParentFile();
            displayFiles();
        } else {
            super.onBackPressed();
        }
    }
}