package com.kapasiya.uploadfileons3bucket.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponseDto<T> {
    private String status;
    private int statusCode;
    private String message;
    private boolean success;
    private T data;
}