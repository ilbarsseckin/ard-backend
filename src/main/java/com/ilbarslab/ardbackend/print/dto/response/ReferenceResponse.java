package com.ilbarslab.ardbackend.print.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ReferenceResponse {
    private UUID id;
    private String name;
    private String sector;
    private String category;
    private String description;
    private String logoUrl;
    private String color;
    private String abbr;
    private Boolean featured;
    private Boolean active;
    private Integer displayOrder;
}
