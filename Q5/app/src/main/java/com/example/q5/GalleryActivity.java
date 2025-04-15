package com.example.q5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private GridView gridView;
    private TextView tvFolderPath;
    private List<String> imagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        tvFolderPath = findViewById(R.id.tvFolderPath);

        // Get the folder path from the intent
        String folderPath = getIntent().getStringExtra("folderPath");
        if (folderPath == null || folderPath.isEmpty()) {
            Toast.makeText(this, "Invalid folder path", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvFolderPath.setText("Folder: " + folderPath);
        loadImagesFromFolder(folderPath);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imagePath = imagePaths.get(position);
                Intent intent = new Intent(GalleryActivity.this, ImageDetailActivity.class);
                intent.putExtra("imagePath", imagePath);
                startActivity(intent);
            }
        });
    }

    private void loadImagesFromFolder(String folderPath) {
        imagePaths.clear();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory() || !folder.canRead()) {
            Toast.makeText(this, "Cannot access folder or folder doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // List only image files
        File[] files = folder.listFiles((dir, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                    lowerName.endsWith(".png") || lowerName.endsWith(".gif");
        });

        if (files != null && files.length > 0) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            for (File file : files) {
                imagePaths.add(file.getAbsolutePath());
            }

            ImageAdapter adapter = new ImageAdapter(this, imagePaths);
            gridView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No images found in this folder", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the gallery when resuming (e.g., after deleting an image)
        String folderPath = getIntent().getStringExtra("folderPath");
        if (folderPath != null && !folderPath.isEmpty()) {
            loadImagesFromFolder(folderPath);
        }
    }
}