package com.example.driveuploader.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Drive Upload App";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private Drive driveService;

    public GoogleDriveService() throws Exception {
        this.driveService = createDriveService();
    }

    private Drive createDriveService() throws Exception {
        InputStream in = new FileInputStream("src/main/resources/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        var flow = new com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        var credential = new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(
                flow, new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver())
                .authorize("user");

        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("upload-", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);

        File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());

        FileContent fileContent = new FileContent(multipartFile.getContentType(), tempFile);

        File uploadedFile = driveService.files().create(fileMetadata, fileContent)
                .setFields("id, name")
                .execute();

        return "Uploaded file: " + uploadedFile.getName() + " (ID: " + uploadedFile.getId() + ")";
    }
}

