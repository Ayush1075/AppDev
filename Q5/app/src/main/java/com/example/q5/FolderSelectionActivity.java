package com.example.q5;


import android.content.Intent;
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

        // Start with external storage directory
        currentDirectory = Environment.getExternalStorageDirectory();
        displayFiles();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File selectedFile = files.get(position);
                if (selectedFile.isDirectory()) {
                    // If directory, navigate into it
                    currentDirectory = selectedFile;
                    displayFiles();
                } else {
                    // If it's a file, ignore (we're only interested in folders)
                    Toast.makeText(FolderSelectionActivity.this, "Please select a folder", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add long press to select folder
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File selectedFile = files.get(position);
                if (selectedFile.isDirectory()) {
                    // Open GalleryActivity with the selected folder path
                    Intent intent = new Intent(FolderSelectionActivity.this, GalleryActivity.class);
                    intent.putExtra("folderPath", selectedFile.getAbsolutePath());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void displayFiles() {
        fileList.clear();
        files.clear();

        tvCurrentPath.setText("Current Path: " + currentDirectory.getAbsolutePath());

        // Add parent directory option if not at root
        if (currentDirectory.getParentFile() != null) {
            fileList.add("../");
            files.add(currentDirectory.getParentFile());
        }

        // Get all directories
        File[] fileArray = currentDirectory.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file.isDirectory()) {
                    fileList.add(file.getName() + "/");
                    files.add(file);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (currentDirectory.getParentFile() != null) {
            currentDirectory = currentDirectory.getParentFile();
            displayFiles();
        } else {
            super.onBackPressed();
        }
    }
}