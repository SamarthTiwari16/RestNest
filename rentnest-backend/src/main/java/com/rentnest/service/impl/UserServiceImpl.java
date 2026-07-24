package com.rentnest.service.impl;

import com.rentnest.dto.response.UserResponse;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.repository.UserRepository;
import com.rentnest.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        return userRepository.findByEmail(email).map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists"));
    }
}
