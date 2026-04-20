package com.aprendemosya.aprendemosya_api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "identifier es obligatorio")
        String identifier,
        @NotBlank(message = "password es obligatorio")
        String password
) {
}
