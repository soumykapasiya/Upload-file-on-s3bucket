package com.kapasiya.uploadfileons3bucket.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    private final String bucketName = "first-bucket-for-survey-app";
    private final AmazonS3 s3Client;

    public String uploadFile(MultipartFile file) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            return "File uploaded : " + fileName;
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            return "File upload failed: " + e.getMessage();
        } finally {
            fileObj.delete();
        }
    }

    public String uploadMultipleFile(List<MultipartFile> files) {
        try{
            files.forEach(this::uploadFile);
        }catch (Exception e) {
            log.error("Error uploading file to S3", e);
            return "File upload failed: " + e.getMessage();
        }
        return "";
    }

    public byte[] downloadFile(String fileName) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("Error downloading file from S3", e);
            return null;
        }
    }

    public String deleteFile(String fileName) {
        try {
            s3Client.deleteObject(bucketName, fileName);
            return fileName + " removed ...";
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            return "File deletion failed: " + e.getMessage();
        }
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
}
