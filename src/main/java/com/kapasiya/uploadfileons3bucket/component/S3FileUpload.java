package com.kapasiya.uploadfileons3bucket.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3FileUpload {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3TransferManager transferManager;
    private final String bucketName = "first-bucket-for-survey-app";

    public String uploadSingleFile(MultipartFile file, String uniqueId) throws IllegalAccessException {
        try {
            log.info("Starting file upload for uniqueId: {}", uniqueId);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = uniqueId + "_" + timestamp;
            log.info("Generated file name: {}", fileName);

            String contentType =
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            log.info("Content type of the file: {}", contentType);

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(contentType)
                            .build();

            s3Client.putObject(
                    putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("File uploaded to S3 successfully with key: {}", fileName);

            String fileUrl = generatePreSignedUrl(s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(fileName)).toString());
            log.info("File URL generated: {}", fileUrl);

            return fileUrl;
        } catch (IOException e) {
            log.error("I/O error occurred while uploading the file for uniqueId: {}", uniqueId, e);
            throw new IllegalAccessException("I/O error while uploading file");
        } catch (S3Exception e) {
            log.error("S3 error occurred while uploading the file for uniqueId: {}", uniqueId, e);
            throw new IllegalAccessException("S3 error while uploading file");
        } catch (SdkClientException e) {
            log.error("Client error occurred while uploading the file for uniqueId: {}", uniqueId, e);
            throw new IllegalAccessException("Client error while uploading file");
        }
    }

    public String generatePreSignedUrl(String objectKey) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PresignedPutObjectRequest resignedRequest = s3Presigner.presignPutObject(builder -> builder
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
        );

        return resignedRequest.url().toString();
    }

    public String uploadSingleFileAsync(
            MultipartFile file, String uniqueId, String folder, String fileName) throws IllegalAccessException {
        try {
            Instant now = Instant.now();
            String year = String.valueOf(now.atZone(ZoneId.of("Asia/Kolkata")).getYear());
            String month = String.format("%02d", now.atZone(ZoneId.of("Asia/Kolkata")).getMonthValue());
            String day = String.format("%02d", now.atZone(ZoneId.of("Asia/Kolkata")).getDayOfMonth());

            String key = folder + "/" + year + "/" + month + "/" + day + "/" + fileName;

            UploadRequest uploadRequest =
                    UploadRequest.builder()
                            .putObjectRequest(
                                    p ->
                                            p.bucket(bucketName)
                                                    .key(key)
                                                    .contentType(
                                                            file.getContentType() != null
                                                                    ? file.getContentType()
                                                                    : "application/octet-stream"))
                            .requestBody(AsyncRequestBody.fromBytes(file.getBytes()))
                            .build();
            log.info("Starting upload to bucket: {} with key: {}", bucketName, key);
            transferManager.upload(uploadRequest);
            String fileUrl = generatePreSignedUrl("https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + key);

            log.info("File uploaded successfully. File URL: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("I/O error occurred while uploading the file for uniqueId: {}", uniqueId, e);
            throw new IllegalAccessException("I/O error while uploading file");
        } catch (Exception e) {
            log.error("Error occurred during file upload for uniqueId: {}", uniqueId, e);
            throw new IllegalAccessException("File upload failed");
        }
    }
}
