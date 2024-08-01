package com.kapasiya.uploadfileons3bucket.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    String uploadFile(MultipartFile file);

    String uploadMultipleFile(List<MultipartFile> files);

    byte[] downloadFile(String fileName);

    String deleteFile(String fileName);
}