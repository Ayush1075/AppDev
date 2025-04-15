package com.example.q5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
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
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imagePreview.setImageBitmap(bitmap);
            tvPhotoPath.setText("Photo saved at: " + currentPhotoPath);
            if (targetFolder != null) {
                btnSavePhoto.setEnabled(true);
            }
        }
    }

    private void handleSelectedFolder(Uri folderUri) {
        try {
            // Use folderUri directly or convert to a File if needed; here we use a simplified approach.
            File folder = new File(folderUri.getPath());
            if (!folder.exists()) {
                folder.mkdirs();
            }
            targetFolder = folder;
            Toast.makeText(this, "Folder selected: " + folder.getAbsolutePath(), Toast.LENGTH_LONG).show();
            if (currentPhotoPath != null) {
                btnSavePhoto.setEnabled(true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error selecting folder", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePhotoToSelectedFolder() {
        if (currentPhotoPath == null || targetFolder == null) {
            Toast.makeText(this, "Take a photo and select a folder first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File sourceFile = new File(currentPhotoPath);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File destFile = new File(targetFolder, "Photo_" + timeStamp + ".jpg");

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

            // Reset UI
            btnSavePhoto.setEnabled(false);
            imagePreview.setImageResource(R.drawable.ic_placeholder);
            tvPhotoPath.setText("");
            currentPhotoPath = null;

        } catch (Exception e) {
            Toast.makeText(this, "Error saving photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
