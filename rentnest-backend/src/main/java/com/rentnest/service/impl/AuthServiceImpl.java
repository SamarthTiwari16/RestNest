package com.rentnest.service.impl;

import com.rentnest.config.JwtProperties;
import com.rentnest.dto.request.LoginRequest;
import com.rentnest.dto.request.RegisterRequest;
import com.rentnest.dto.response.AuthResponse;
import com.rentnest.dto.response.UserResponse;
import com.rentnest.entity.Role;
import com.rentnest.entity.User;
import com.rentnest.exception.DuplicateResourceException;
import com.rentnest.repository.UserRepository;
import com.rentnest.security.JwtTokenProvider;
import com.rentnest.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        User user = new User(request.name().trim(), email, normalizePhone(request.phone()), passwordEncoder.encode(request.password()), Role.ROLE_USER);
        User savedUser = userRepository.save(user);
        log.info("User registered: userId={}", savedUser.getId());
        return createAuthResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
        log.info("User logged in: userId={}", user.getId());
        return createAuthResponse(user);
    }

    private AuthResponse createAuthResponse(User user) {
        return new AuthResponse(jwtTokenProvider.generateToken(user), "Bearer", Instant.now().plusMillis(jwtProperties.expirationMs()), UserResponse.from(user));
    }

    private String normalizePhone(String phone) {
        return phone.startsWith("+91") ? phone : "+91" + phone;
    }
}
