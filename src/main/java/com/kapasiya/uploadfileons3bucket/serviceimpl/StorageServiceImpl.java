package com.kapasiya.uploadfileons3bucket.serviceimpl;

import com.kapasiya.uploadfileons3bucket.component.S3FileUpload;
import com.kapasiya.uploadfileons3bucket.dto.CustomResponseDto;
import com.kapasiya.uploadfileons3bucket.entity.UploadedFile;
import com.kapasiya.uploadfileons3bucket.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final S3FileUpload s3FileUpload;

    @Override
    public CustomResponseDto<Void> uploadFile(MultipartFile file) {
        CustomResponseDto<Void> response = new CustomResponseDto<>();

        File convertedFile = convertMultiPartFileToFile(file);

        try {
            String uniqueId = String.valueOf(Instant.now().toEpochMilli());

            String fileUrl = s3FileUpload.uploadSingleFileAsync(
                    file, uniqueId, "response", file.getOriginalFilename() + uniqueId);

            log.info("File uploaded successfully. File URL: {}", fileUrl);

            response.setMessage("File uploaded successfully");
            response.setStatus("Success");

        } catch (IllegalAccessException e) {
            log.error("File upload failed", e);

            response.setMessage("File upload failed: " + e.getMessage());
            response.setStatus("Filed");

        } finally {
            if (convertedFile.exists()) {
                convertedFile.delete();
            }
        }
        return response;
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    @Override
    public List<UploadedFile> uploadFiles(MultipartFile[] files, String recordId) throws IllegalAccessException {

        CopyOnWriteArrayList<UploadedFile> uploadedFiles = new CopyOnWriteArrayList<>();
        for (MultipartFile file : files) {
            String url = uploadFilesToCloud(file, recordId);
            UploadedFile uploadedFile =
                    UploadedFile.builder()
                            .fileName(file.getOriginalFilename())
                            .url(url)
                            .contentType(file.getContentType())
                            .size(file.getSize())
                            .createdAt(Instant.now())
                            .build();
            uploadedFiles.add(uploadedFile);
        }

        return uploadedFiles;
    }

    private String uploadFilesToCloud(MultipartFile file, String uniqueId) throws IllegalAccessException {
        return s3FileUpload.uploadSingleFileAsync(
                file, uniqueId, "response", file.getOriginalFilename() + uniqueId);
    }
}
