package com.example.q5;



import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private GridView gridView;
    private TextView tvFolderPath;
    private String folderPath;
    private List<String> imagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        tvFolderPath = findViewById(R.id.tvFolderPath);

        // Get the folder path from the intent
        folderPath = getIntent().getStringExtra("folderPath");
        if (folderPath == null || folderPath.isEmpty()) {
            Toast.makeText(this, "Invalid folder path", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvFolderPath.setText("Folder: " + folderPath);
        loadImagesFromFolder();

        // Set click listener for grid items
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String imagePath = imagePaths.get(position);
            Intent intent = new Intent(GalleryActivity.this, ImageDetailActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        });
    }

    private void loadImagesFromFolder() {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            Toast.makeText(this, "Folder does not exist", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            Toast.makeText(this, "No files found in the folder", Toast.LENGTH_SHORT).show();
            return;
        }

        imagePaths.clear();

        // Filter only image files
        for (File file : files) {
            if (isImageFile(file.getName())) {
                imagePaths.add(file.getAbsolutePath());
            }
        }

        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "No images found in the folder", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageAdapter adapter = new ImageAdapter(this, imagePaths);
        gridView.setAdapter(adapter);
    }

    private boolean isImageFile(String fileName) {
        String extension = fileName.toLowerCase();
        return extension.endsWith(".jpg") || extension.endsWith(".jpeg") ||
                extension.endsWith(".png") || extension.endsWith(".gif") ||
                extension.endsWith(".bmp");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload images when returning to this activity (e.g., after deletion)
        loadImagesFromFolder();
    }
}
