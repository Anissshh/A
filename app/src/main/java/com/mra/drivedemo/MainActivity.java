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

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.InputStream;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_SIGN_IN = 100;
    private GoogleSignInAccount googleAccount;
    private DriveServiceHelper driveServiceHelper;

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

        btnCreateProject.setOnClickListener(v -> signIn());

        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(this, "Upload Done — Please check your Drive", Toast.LENGTH_LONG).show();
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void signIn() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), REQ_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGN_IN && resultCode == RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                googleAccount = task.getResult();
                driveServiceHelper = new DriveServiceHelper(this, googleAccount);

                String projectName = etProjectName.getText().toString().trim();
                driveServiceHelper.createFolder(projectName, null).addOnSuccessListener(folderId -> {
                    projectFolderId = folderId;
                    for (String cat : categories) {
                        driveServiceHelper.createFolder(cat, projectFolderId).addOnSuccessListener(subId -> {
                            subFolderIds.put(cat, subId);
                        });
                    }
                    Toast.makeText(this, "Project folders created in Drive", Toast.LENGTH_LONG).show();
                });
            }
        }
    }

    private void uploadImage() {
        if (pickedImageUri == null || projectFolderId == null) return;
        String category = spinnerCategory.getSelectedItem().toString();
        String parentId = subFolderIds.get(category);
        if (parentId == null) return;
        try {
            InputStream inputStream = getContentResolver().openInputStream(pickedImageUri);
            driveServiceHelper.uploadFile(inputStream, "photo_"+System.currentTimeMillis()+".jpg", "image/jpeg", parentId)
                    .addOnSuccessListener(fileId -> {
                        Toast.makeText(this, "Uploaded to " + category, Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
