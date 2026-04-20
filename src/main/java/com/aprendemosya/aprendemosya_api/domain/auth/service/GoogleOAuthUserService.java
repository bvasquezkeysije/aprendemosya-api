package com.aprendemosya.aprendemosya_api.domain.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.aprendemosya.aprendemosya_api.common.exception.ApiException;
import com.aprendemosya.aprendemosya_api.domain.auth.dto.LoginResponse;
import com.aprendemosya.aprendemosya_api.domain.user.entity.AppUser;
import com.aprendemosya.aprendemosya_api.domain.user.entity.UserProfile;
import com.aprendemosya.aprendemosya_api.domain.user.repository.AppUserRepository;
import com.aprendemosya.aprendemosya_api.domain.user.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleOAuthUserService {

    @NonNull
    private static final MediaType FORM_URLENCODED = MediaType.APPLICATION_FORM_URLENCODED;

    private final RestClient restClient;
    private final AppUserRepository appUserRepository;
    private final UserProfileRepository userProfileRepository;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    public GoogleOAuthUserService(
            AppUserRepository appUserRepository,
            UserProfileRepository userProfileRepository,
            RestClient.Builder restClientBuilder
    ) {
        this.restClient = restClientBuilder.build();
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

    public LoginResponse loginWithGoogleCode(String code, String redirectUri) {
        GoogleTokenResponse tokenResponse = exchangeCode(code, redirectUri);
        GoogleUserInfoResponse userInfo = fetchUserInfo(tokenResponse.accessToken());
        AppUser user = syncGoogleUser(
                userInfo.email(),
                userInfo.givenName(),
                userInfo.familyName(),
                userInfo.name(),
                userInfo.picture()
        );

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                Boolean.TRUE.equals(user.getActive()),
                user.getProfile() != null ? user.getProfile().getProfileImageUrl() : null
        );
    }

    public AppUser syncGoogleUser(
            String email,
            String givenName,
            String familyName,
            String fullName,
            String picture
    ) {
        if (email == null || email.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Google no envio el email");
        }

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
        savedUser.setProfile(profile);
        return savedUser;
    }

    private GoogleTokenResponse exchangeCode(String code, String redirectUri) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", googleClientId);
        formData.add("client_secret", googleClientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("grant_type", "authorization_code");

        GoogleTokenResponse tokenResponse = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(GoogleTokenResponse.class);

        if (tokenResponse == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "No se pudo obtener el token de Google");
        }

        String accessToken = tokenResponse.accessToken();

        if (accessToken == null || accessToken.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "No se pudo obtener el token de Google");
        }

        return tokenResponse;
    }

    private GoogleUserInfoResponse fetchUserInfo(@NonNull String accessToken) {
        GoogleUserInfoResponse userInfo = restClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(GoogleUserInfoResponse.class);

        if (userInfo == null || userInfo.email() == null || userInfo.email().isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "No se pudo leer el perfil de Google");
        }

        return userInfo;
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

    private record GoogleTokenResponse(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("token_type")
            String tokenType,
            @JsonProperty("expires_in")
            Long expiresIn,
            String scope,
            @JsonProperty("refresh_token")
            String refreshToken,
            @JsonProperty("id_token")
            String idToken
    ) {
    }

    private record GoogleUserInfoResponse(
            String sub,
            String email,
            @JsonProperty("email_verified")
            Boolean emailVerified,
            String name,
            @JsonProperty("given_name")
            String givenName,
            @JsonProperty("family_name")
            String familyName,
            String picture
    ) {
    }
}
