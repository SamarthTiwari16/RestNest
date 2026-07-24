package com.rentnest.service;

import com.rentnest.dto.request.LoginRequest;
import com.rentnest.dto.request.RegisterRequest;
import com.rentnest.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
