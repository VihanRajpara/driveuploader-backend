package com.example.driveuploader.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.driveuploader.service.GoogleDriveService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String result = googleDriveService.uploadFile(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
