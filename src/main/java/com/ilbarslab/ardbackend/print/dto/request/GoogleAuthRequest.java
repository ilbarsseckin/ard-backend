package com.ilbarslab.ardbackend.print.dto.request;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String code;
    private String redirectUri;
}