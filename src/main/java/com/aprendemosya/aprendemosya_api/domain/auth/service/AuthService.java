package com.aprendemosya.aprendemosya_api.domain.auth.service;

import com.aprendemosya.aprendemosya_api.common.exception.ApiException;
import com.aprendemosya.aprendemosya_api.domain.auth.dto.LoginRequest;
import com.aprendemosya.aprendemosya_api.domain.auth.dto.LoginResponse;
import com.aprendemosya.aprendemosya_api.domain.auth.dto.UserProfileResponse;
import com.aprendemosya.aprendemosya_api.domain.auth.repository.AuthQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthQueryRepository authQueryRepository;
    private final GoogleOAuthUserService googleOAuthUserService;

    public AuthService(
            AuthQueryRepository authQueryRepository,
            GoogleOAuthUserService googleOAuthUserService
    ) {
        this.authQueryRepository = authQueryRepository;
        this.googleOAuthUserService = googleOAuthUserService;
    }

    public LoginResponse login(LoginRequest request) {
        return authQueryRepository
                .login(request.identifier(), request.password())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));
    }

    public LoginResponse loginWithGoogleCode(String code, String redirectUri) {
        return googleOAuthUserService.loginWithGoogleCode(code, redirectUri);
    }

    public UserProfileResponse getProfileByUserId(Long userId) {
        return authQueryRepository
                .findProfileByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
