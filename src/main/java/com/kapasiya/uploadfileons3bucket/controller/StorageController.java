package com.kapasiya.uploadfileons3bucket.controller;

import com.kapasiya.uploadfileons3bucket.dto.CustomResponseDto;
import com.kapasiya.uploadfileons3bucket.entity.UploadedFile;
import com.kapasiya.uploadfileons3bucket.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<CustomResponseDto<Void>> uploadFile(@RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok().body(service.uploadFile(file));
    }
    @PostMapping("/upload/multi")
    public ResponseEntity<List<UploadedFile>> uploadFiles(@RequestParam("files") MultipartFile[] files,String recordId) throws IllegalAccessException {
        return ResponseEntity.ok().body(service.uploadFiles(files,recordId));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        return null;
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return null;
    }
}
