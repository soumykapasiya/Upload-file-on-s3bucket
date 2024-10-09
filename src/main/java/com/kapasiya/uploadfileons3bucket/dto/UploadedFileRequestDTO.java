package com.kapasiya.uploadfileons3bucket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFileRequestDTO {
    private String fileName;
    private String url;
    private String contentType;
    private long size;
}
