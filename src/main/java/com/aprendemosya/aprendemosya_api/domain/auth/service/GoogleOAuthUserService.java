package com.aprendemosya.aprendemosya_api.domain.auth.service;

import com.aprendemosya.aprendemosya_api.common.exception.ApiException;
import com.aprendemosya.aprendemosya_api.domain.user.entity.AppUser;
import com.aprendemosya.aprendemosya_api.domain.user.entity.UserProfile;
import com.aprendemosya.aprendemosya_api.domain.user.repository.AppUserRepository;
import com.aprendemosya.aprendemosya_api.domain.user.repository.UserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleOAuthUserService {

    private final AppUserRepository appUserRepository;
    private final UserProfileRepository userProfileRepository;

    public GoogleOAuthUserService(
            AppUserRepository appUserRepository,
            UserProfileRepository userProfileRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public AppUser syncGoogleUser(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "No se pudo validar la cuenta de Google");
        }

        String email = readRequiredAttribute(oauthUser, "email");
        String givenName = readOptionalAttribute(oauthUser, "given_name");
        String familyName = readOptionalAttribute(oauthUser, "family_name");
        String picture = readOptionalAttribute(oauthUser, "picture");
        String fullName = readOptionalAttribute(oauthUser, "name");

        AppUser user = appUserRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(email, givenName, familyName, fullName));

        user.setActive(true);
        AppUser savedUser = appUserRepository.save(user);

        UserProfile profile = userProfileRepository.findByUserId(savedUser.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(savedUser);
                    return newProfile;
                });

        if (givenName != null && !givenName.isBlank()) {
            profile.setFirstName(givenName);
        }

        if (familyName != null && !familyName.isBlank()) {
            profile.setLastName(familyName);
        }

        if ((profile.getFirstName() == null || profile.getFirstName().isBlank())
                && fullName != null && !fullName.isBlank()) {
            profile.setFirstName(fullName);
        }

        if (picture != null && !picture.isBlank()) {
            profile.setProfileImageUrl(picture);
        }

        userProfileRepository.save(profile);
        return savedUser;
    }

    private AppUser createGoogleUser(String email, String givenName, String familyName, String fullName) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setUsername(generateUsername(email, givenName, familyName, fullName));
        user.setPasswordHash("google-oauth-" + UUID.randomUUID());
        user.setRole("USER");
        user.setActive(true);
        return user;
    }

    private String generateUsername(String email, String givenName, String familyName, String fullName) {
        String baseSource = firstNonBlank(givenName, fullName, email.split("@")[0]);
        String normalized = normalizeUsername(baseSource);

        if (normalized.isBlank()) {
            normalized = "user";
        }

        String candidate = normalized;
        int suffix = 1;

        while (appUserRepository.existsByUsername(candidate)) {
            candidate = normalized + suffix;
            suffix++;
        }

        return candidate;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return "user";
    }

    private String normalizeUsername(String value) {
        String sanitized = value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");

        if (sanitized.length() > 40) {
            return sanitized.substring(0, 40);
        }

        return sanitized;
    }

    private String readRequiredAttribute(OAuth2User oauthUser, String key) {
        Object value = oauthUser.getAttributes().get(key);

        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Google no envio el dato requerido: " + key);
        }

        return stringValue;
    }

    private String readOptionalAttribute(OAuth2User oauthUser, String key) {
        Map<String, Object> attributes = oauthUser.getAttributes();
        Object value = attributes.get(key);
        return value instanceof String stringValue ? stringValue : null;
    }
}
