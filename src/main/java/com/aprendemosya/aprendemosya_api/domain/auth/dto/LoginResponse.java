package com.aprendemosya.aprendemosya_api.domain.auth.dto;

public record LoginResponse(
        Long userId,
        String username,
        String email,
        String role,
        Boolean active,
        String profileImageUrl
) {
}
