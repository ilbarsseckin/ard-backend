package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @NotBlank
    private String addressLine;

    @NotBlank
    private String district;

    @NotBlank
    private String city;

    private String country = "Türkiye";
    private Boolean isDefault = false;
}
