package com.kapasiya.uploadfileons3bucket.mapper;

import com.kapasiya.uploadfileons3bucket.dto.UploadedFileRequestDTO;
import com.kapasiya.uploadfileons3bucket.entity.UploadedFile;

public class UploadedFileMappert {

    public static UploadedFile toEntity(UploadedFileRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        return UploadedFile.builder()
                .fileName(requestDTO.getFileName())
                .url(requestDTO.getUrl())
                .contentType(requestDTO.getContentType())
                .size(requestDTO.getSize())
                .build();
    }

    public static UploadedFileRequestDTO toDTO(UploadedFile entity) {
        if (entity == null) {
            return null;
        }
        return UploadedFileRequestDTO.builder()
                .fileName(entity.getFileName())
                .url(entity.getUrl())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .build();
    }
}
