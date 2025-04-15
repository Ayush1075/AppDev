package com.example.q5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvImageName;
    private TextView tvImagePath;
    private TextView tvImageSize;
    private TextView tvImageDate;
    private Button btnDelete;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.detailImageView);
        tvImageName = findViewById(R.id.tvImageName);
        tvImagePath = findViewById(R.id.tvImagePath);
        tvImageSize = findViewById(R.id.tvImageSize);
        tvImageDate = findViewById(R.id.tvImageDate);
        btnDelete = findViewById(R.id.btnDelete);

        // Get the image path from the intent
        imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(this, "Invalid image path", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadImageDetails();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void loadImageDetails() {
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            Toast.makeText(this, "Image file does not exist", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load and display the image
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);

        // Display image details
        tvImageName.setText("Name: " + imageFile.getName());
        tvImagePath.setText("Path: " + imageFile.getAbsolutePath());
        tvImageSize.setText("Size: " + formatFileSize(imageFile.length()));
        tvImageDate.setText("Date: " + formatLastModifiedDate(imageFile.lastModified()));
    }

    private String formatFileSize(long size) {
        final double KB = 1024.0;
        final double MB = KB * 1024;

        if (size < KB) {
            return size + " B";
        } else if (size < MB) {
            return String.format(Locale.getDefault(), "%.2f KB", size / KB);
        } else {
            return String.format(Locale.getDefault(), "%.2f MB", size / MB);
        }
    }

    private String formatLastModifiedDate(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timeMillis));
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Image");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImage();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void deleteImage() {
        File file = new File(imagePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show();

                // Notify the media scanner about the deletion
                getContentResolver().delete(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        android.provider.MediaStore.Images.Media.DATA + "=?",
                        new String[] { imagePath }
                );

                finish(); // Return to the gallery
            } else {
                Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image does not exist", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
