package com.av.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.av.services.FileStorageService;

@RestController
@RequestMapping("/files")
public class FileController {

  @Autowired
  private FileStorageService fileStorageService;

  // Upload API
  @PostMapping("/upload")
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
    try {
      String fileId = fileStorageService.uploadFile(file);
      return ResponseEntity.ok("File stored with ID: " + fileId);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
    }
  }

  // Download API
  @GetMapping("/download/{id}")
  public ResponseEntity<?> download(@PathVariable("id") String id) {
    return fileStorageService.downloadFile(id).map(resource -> {
      try {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resource.getFilename() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(new InputStreamResource(resource.getInputStream()));
      } catch (Exception e) {
        return ResponseEntity.status(500).body("Download error: " + e.getMessage());
      }
    }).orElse(ResponseEntity.notFound().build());
  }
}
