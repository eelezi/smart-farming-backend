package com.timmk22.smartfarming.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 100) String name,
        @Email @NotBlank @Size(max = 120) String email,
        @NotBlank @Size(min = 6, max = 255) String password
) {}