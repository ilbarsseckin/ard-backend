package com.ilbarslab.ardbackend.print.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String s3Key;
    private String originalName;
    private int pageCount;
    private boolean pageWarning;
    private String warningMessage;
}
