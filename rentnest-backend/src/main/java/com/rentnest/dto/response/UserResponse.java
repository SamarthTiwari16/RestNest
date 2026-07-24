package com.rentnest.dto.response;

import com.rentnest.entity.User;

import java.time.Instant;

public record UserResponse(Long id, String name, String email, String phone, String role, Instant createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name(), user.getCreatedAt());
    }
}
