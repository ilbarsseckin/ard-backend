package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateRoleRequest {

    @NotBlank
    private String name;

    private String description;

    private Set<UUID> permissionIds;
}