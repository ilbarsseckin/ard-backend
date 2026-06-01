package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogOrderFileResponse {
    private UUID id;
    private String originalName;
    private Long fileSize;
    private String mimeType;
    private Integer pageCount;
    private Boolean pageWarning;
    private Instant createdAt;
    /** Download endpoint URL */
    private String downloadUrl;
}
