package com.ilbarslab.ardbackend.print.dto.request;

import lombok.Data;
import java.time.Instant;

@Data
public class AnnouncementBarRequest {
    private String message;
    private String subMessage;
    private String couponCode;
    private String bgColor;
    private String textColor;
    private Instant endsAt;
    private Boolean active;
    private Integer sortOrder;
}
