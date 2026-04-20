package com.aprendemosya.aprendemosya_api.domain.auth.controller;

import com.aprendemosya.aprendemosya_api.common.response.ApiResponse;
import com.aprendemosya.aprendemosya_api.domain.auth.dto.LoginRequest;
import com.aprendemosya.aprendemosya_api.domain.auth.dto.LoginResponse;
import com.aprendemosya.aprendemosya_api.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login correcto", authService.login(request));
    }
}
