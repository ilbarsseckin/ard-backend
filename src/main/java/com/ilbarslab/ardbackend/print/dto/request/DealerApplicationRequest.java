package com.ilbarslab.ardbackend.print.dto.request;

import lombok.Data;

@Data
public class DealerApplicationRequest {
    private String companyName;
    private String taxNumber;
    private String taxOffice;
    private String phone;
    private String address;
    private String city;
    private String district;
    private String estimatedMonthlyRevenue;
    private String businessType;
    private String website;
    private String note;
}