package com.mra.drivedemo;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Context context, GoogleSignInAccount account) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton("https://www.googleapis.com/auth/drive.file"));
        credential.setSelectedAccount(account.getAccount());
        mDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("Mr.A")
                .build();
    }

    public com.google.android.gms.tasks.Task<String> createFolder(String name, String parentId) {
        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setName(name)
                    .setMimeType("application/vnd.google-apps.folder");
            if (parentId != null) {
                metadata.setParents(java.util.Collections.singletonList(parentId));
            }
            File folder = mDriveService.files().create(metadata).setFields("id").execute();
            return folder.getId();
        });
    }

    public com.google.android.gms.tasks.Task<String> uploadFile(InputStream inputStream, String name, String mimeType, String parentId) {
        return Tasks.call(mExecutor, () -> {
            File fileMetadata = new File().setName(name);
            if (parentId != null) {
                fileMetadata.setParents(java.util.Collections.singletonList(parentId));
            }
            com.google.api.client.http.InputStreamContent content =
                    new com.google.api.client.http.InputStreamContent(mimeType, inputStream);
            File file = mDriveService.files().create(fileMetadata, content)
                    .setFields("id").execute();
            return file.getId();
        });
    }
}
