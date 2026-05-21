package com.ilbarslab.ardbackend.print.dto.request;

import com.ilbarslab.ardbackend.print.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private String phone;

    private Role role = Role.CUSTOMER;
}