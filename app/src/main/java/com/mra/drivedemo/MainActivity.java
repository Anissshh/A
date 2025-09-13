package com.mra.drivedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.InputStream;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    EditText etProjectName;
    Spinner spinnerCategory;
    Button btnPickImage, btnCreateProject, btnSubmit;
    Uri pickedImageUri;
    String projectFolderId = null;
    Map<String,String> subFolderIds = new HashMap<>();

    final List<String> categories = Arrays.asList("Tower","S1M","S2M","Clutter","Azimuth");

    ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etProjectName = findViewById(R.id.etProjectName);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnCreateProject = findViewById(R.id.btnCreateProject);
        btnSubmit = findViewById(R.id.btnSubmit);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        pickedImageUri = result.getData().getData();
                        Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                        uploadImage();
                    }
                });

        btnPickImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            } else {
                pickImage();
            }
        });

        btnCreateProject.setOnClickListener(v -> {
            Toast.makeText(this, "Project Created (dummy)", Toast.LENGTH_SHORT).show();
        });

        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(this, "Upload Done — Please check your Drive (dummy)", Toast.LENGTH_LONG).show();
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void uploadImage() {
        if (pickedImageUri == null) return;
        String category = spinnerCategory.getSelectedItem().toString();
        Toast.makeText(this, "Image uploaded to " + category + " (dummy)", Toast.LENGTH_SHORT).show();
    }
}
