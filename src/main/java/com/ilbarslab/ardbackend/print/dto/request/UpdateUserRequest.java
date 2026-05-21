package com.ilbarslab.ardbackend.print.dto.request;

import com.ilbarslab.ardbackend.print.entity.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank
    private String name;

    private String phone;

    private Role role;
}