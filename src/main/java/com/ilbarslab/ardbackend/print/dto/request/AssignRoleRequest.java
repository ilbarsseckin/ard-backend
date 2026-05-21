package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignRoleRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID appRoleId;
}