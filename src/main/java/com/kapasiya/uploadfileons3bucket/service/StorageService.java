package com.kapasiya.uploadfileons3bucket.service;

import com.kapasiya.uploadfileons3bucket.dto.CustomResponseDto;
import com.kapasiya.uploadfileons3bucket.entity.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    CustomResponseDto<Void> uploadFile(MultipartFile file);

    List<UploadedFile> uploadFiles(MultipartFile[] files, String recordId) throws IllegalAccessException;
}