package com.aprendemosya.aprendemosya_api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleCodeLoginRequest(
        @NotBlank(message = "El codigo de Google es obligatorio")
        String code,

        @NotBlank(message = "La URL de origen es obligatoria")
        String redirectUri
) {
}
