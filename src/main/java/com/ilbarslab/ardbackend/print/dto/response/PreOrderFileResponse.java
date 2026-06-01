package com.ilbarslab.ardbackend.print.dto.response;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.PreOrderFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreOrderFileResponse {
    private UUID id;
    private String originalName;
    private Long fileSize;
    private String mimeType;
    private String downloadUrl;
    private Instant createdAt;

    public static PreOrderFileResponse from(PreOrderFile f, String baseUrl) {
        return PreOrderFileResponse.builder()
                .id(f.getId())
                .originalName(f.getOriginalName())
                .fileSize(f.getFileSize())
                .mimeType(f.getMimeType())
                .downloadUrl(baseUrl + "/uploads/design/" + f.getId() + extOf(f.getOriginalName()))
                .createdAt(f.getCreatedAt())
                .build();
    }

    private static String extOf(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i) : "";
    }
}
