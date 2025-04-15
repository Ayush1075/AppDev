package com.example.q5;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_FOLDER = 2;

    private String currentPhotoPath;
    private File targetFolder;
    private ImageView imagePreview;
    private TextView tvPhotoPath;
    private Button btnSavePhoto;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imagePreview = findViewById(R.id.imagePreview);
        tvPhotoPath = findViewById(R.id.tvPhotoPath);
        btnSavePhoto = findViewById(R.id.btnSavePhoto);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnSelectFolder = findViewById(R.id.btnSelectFolder);

        btnTakePhoto.setOnClickListener(view -> dispatchTakePictureIntent());
        btnSelectFolder.setOnClickListener(view -> openFolderPicker());
        btnSavePhoto.setOnClickListener(view -> savePhotoToSelectedFolder());

        btnSavePhoto.setEnabled(false);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure there is a camera activity available
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                // Use the correct FileProvider authority from the manifest
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.q5.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Add flags to grant temporary access to the URI
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_PICK_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            displayCapturedPhoto();
        } else if (requestCode == REQUEST_PICK_FOLDER && resultCode == RESULT_OK && data != null) {
            handleSelectedFolder(data.getData());
        }
    }

    private void displayCapturedPhoto() {
        if (currentPhotoPath != null) {
            // Set options to reduce memory usage
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // Reduce image size by factor of 4

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
            if (bitmap != null) {
                imagePreview.setImageBitmap(bitmap);
                tvPhotoPath.setText("Photo saved at: " + currentPhotoPath);
                if (targetFolder != null) {
                    btnSavePhoto.setEnabled(true);
                }
            } else {
                Toast.makeText(this, "Failed to load the captured image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSelectedFolder(Uri folderUri) {
        try {
            // Take persistable permission for the folder
            int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(folderUri, takeFlags);

            // Store the URI for later use
            targetFolder = new File(folderUri.toString());
            Toast.makeText(this, "Folder selected: " + folderUri.toString(), Toast.LENGTH_LONG).show();

            if (currentPhotoPath != null) {
                btnSavePhoto.setEnabled(true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error selecting folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void savePhotoToSelectedFolder() {
        if (currentPhotoPath == null || targetFolder == null) {
            Toast.makeText(this, "Take a photo and select a folder first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // For Android 10+ (API 29+), use MediaStore
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageUsingMediaStore();
            } else {
                // For older versions, directly save to the file system
                saveImageToFileSystem();
            }

            // Reset UI
            btnSavePhoto.setEnabled(false);
            imagePreview.setImageResource(R.drawable.ic_placeholder);
            tvPhotoPath.setText("");
            currentPhotoPath = null;

        } catch (Exception e) {
            Toast.makeText(this, "Error saving photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUsingMediaStore() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Photo_" + timeStamp + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CameraApp");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri != null) {
            try (OutputStream out = getContentResolver().openOutputStream(imageUri)) {
                // Compress and save the bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                Toast.makeText(this, "Photo saved to gallery", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveImageToFileSystem() throws IOException {
        File sourceFile = new File(currentPhotoPath);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Create a Pictures directory if using a content:// URI
        File picturesDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraApp");
        if (!picturesDir.exists()) {
            picturesDir.mkdirs();
        }

        File destFile = new File(picturesDir, "Photo_" + timeStamp + ".jpg");

        // Copy the file by compressing the bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        FileOutputStream out = new FileOutputStream(destFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();

        Toast.makeText(this, "Photo saved to: " + destFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

        // Update gallery by broadcasting a media scan intent
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(destFile);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
}