package com.aprendemosya.aprendemosya_api.domain.auth.dto;

public record UserProfileResponse(
        Long userId,
        String username,
        String email,
        String role,
        boolean active,
        String profileImageUrl,
        String firstName,
        String lastName,
        String displayName
) {
}
