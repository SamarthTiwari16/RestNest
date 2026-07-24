package com.rentnest.service;

import com.rentnest.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);
}
